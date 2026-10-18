package com.mftplus.mftonlinestoreee.inventories.service.impl;

import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryTransactionRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryTransaction;
import com.mftplus.mftonlinestoreee.inventories.entity.enums.InventoryTransactionType;
import com.mftplus.mftonlinestoreee.inventories.exception.InsufficientStockException;
import com.mftplus.mftonlinestoreee.inventories.exception.InvalidQuantityException;
import com.mftplus.mftonlinestoreee.inventories.mapper.InventoryTransactionMapper;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryItemRepository;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryTransactionRepository;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryTransactionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Loggable
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    @Inject
    private InventoryTransactionRepository repository;

    @Inject
    private InventoryItemRepository inventoryItemRepository;

    @Inject
    private InventoryTransactionMapper mapper;

    @Override
    public List<InventoryTransactionDto> findByItem(Long itemId) {

        return repository.findByItem(itemId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Optional<InventoryTransactionDto> register(
            Long itemId,
            InventoryTransactionRequest request
    ) {

        Optional<InventoryItem> optionalItem =
                inventoryItemRepository.findById(itemId);

        if (optionalItem.isEmpty()) {
            return Optional.empty();
        }

        InventoryItem item = optionalItem.get();

        applyQuantity(item, request);

        inventoryItemRepository.update(item);

        InventoryTransaction transaction =
                mapper.toEntity(request);

        transaction.setInventoryItem(item);

        repository.save(transaction);

        return Optional.of(mapper.toDto(transaction));
    }

    private void applyQuantity(
            InventoryItem item,
            InventoryTransactionRequest request
    ) {

        int quantity = request.quantity();

        InventoryTransactionType type =
                request.type();

        if (type == InventoryTransactionType.IN ||
                type == InventoryTransactionType.OUT ||
                type == InventoryTransactionType.RETURN) {

            if (quantity <= 0) {
                throw new InvalidQuantityException(
                        "Quantity must be greater than zero"
                );
            }
        }

        if (type == InventoryTransactionType.ADJUSTMENT &&
                quantity == 0) {

            throw new InvalidQuantityException(
                    "Adjustment quantity cannot be zero"
            );
        }

        int newQuantity;

        switch (type) {

            case IN:
                newQuantity = item.getQuantity() + quantity;
                break;

            case RETURN:
                newQuantity = item.getQuantity() + quantity;
                break;

            case OUT:
                newQuantity = item.getQuantity() - quantity;
                break;

            case ADJUSTMENT:
                newQuantity = item.getQuantity() + quantity;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported transaction type: " + type
                );
        }

        if (newQuantity < 0) {
            throw new InsufficientStockException(
                    "Insufficient stock for item: " +
                            item.getId()
            );
        }

        item.setQuantity(newQuantity);
    }
}