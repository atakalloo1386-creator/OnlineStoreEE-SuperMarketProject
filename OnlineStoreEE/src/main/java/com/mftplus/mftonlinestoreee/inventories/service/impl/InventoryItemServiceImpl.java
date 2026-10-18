package com.mftplus.mftonlinestoreee.inventories.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryItemRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.entity.InventoryItem;
import com.mftplus.mftonlinestoreee.inventories.exception.DuplicateInventoryItemException;
import com.mftplus.mftonlinestoreee.inventories.exception.InventoryNotFoundException;
import com.mftplus.mftonlinestoreee.inventories.exception.ProductNotFoundException;
import com.mftplus.mftonlinestoreee.inventories.exception.StockNotEmptyException;
import com.mftplus.mftonlinestoreee.inventories.mapper.InventoryItemMapper;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryItemRepository;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryRepository;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryItemService;
import com.mftplus.mftonlinestoreee.products.entity.Product;
import com.mftplus.mftonlinestoreee.products.repository.ProductRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@Loggable
public class InventoryItemServiceImpl implements InventoryItemService {
    @Inject
    private InventoryItemRepository repository;

    @Inject
    private InventoryRepository inventoryRepository;

    @Inject
    private ProductRepository productRepository;

    @Inject
    private InventoryItemMapper mapper;

    @Transactional
    @Override
    public InventoryItemDto create(InventoryItemRequest request) {

        Inventory inventory = loadInventory(request.inventoryId());

        Product product = loadProduct(request.productId());

        checkIsUnique(request.inventoryId(), request.productId(), null);

        InventoryItem item = mapper.toEntity(request);

        item.setInventory(inventory);
        item.setProduct(product);
        item.setQuantity(0);

        repository.save(item);

        return mapper.toDto(item);
    }

    @Override
    public Optional<InventoryItemDto> findById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public List<InventoryItemDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public PageResult<InventoryItemDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<InventoryItemDto> update(Long id, InventoryItemRequest request) {

        Inventory inventory = loadInventory(request.inventoryId());

        Product product = loadProduct(request.productId());

        checkIsUnique(request.inventoryId(), request.productId(), id);

        return repository.findById(id)
                .map(item -> {
                    item.setInventory(inventory);
                    item.setProduct(product);
                    item.setMinimumQuantity(request.minimumQuantity());
                    item.setBatchNumber(request.batchNumber());
                    item.setExpirationDate(request.expirationDate());
                    repository.update(item);
                    return mapper.toDto(item);
                });
    }

    @Transactional
    @Override
    public Optional<InventoryItemDto> deleteById(Long id) {

        return repository.findById(id)
                .map(item -> {

                    if (item.getQuantity() != 0) {
                        throw new StockNotEmptyException(
                                "Inventory item cannot be deleted while stock is not zero: " + id
                        );
                    }

                    repository.delete(item);

                    return mapper.toDto(item);
                });
    }

    @Override
    public List<InventoryItemDto> findByInventory(Long inventoryId) {

        return repository.findByInventory(inventoryId)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryItemDto> findLowStock() {

        return repository.findLowStock()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private Inventory loadInventory(Long inventoryId) {

        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found: " + inventoryId
                        )
                );
    }

    private Product loadProduct(Long productId) {

        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found: " + productId
                        )
                );
    }

    private void checkIsUnique(
            Long inventoryId,
            Long productId,
            Long currentId
    ) {

        repository.findByInventoryAndProduct(inventoryId, productId)
                .ifPresent(existing -> {

                    if (currentId == null ||
                            !existing.getId().equals(currentId)) {

                        throw new DuplicateInventoryItemException(
                                "Product already exists in this inventory"
                        );
                    }
                });
    }
}
