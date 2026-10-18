package com.mftplus.mftonlinestoreee.inventories.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.logging.Loggable;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryDto;
import com.mftplus.mftonlinestoreee.inventories.dto.InventoryRequest;
import com.mftplus.mftonlinestoreee.inventories.entity.Inventory;
import com.mftplus.mftonlinestoreee.inventories.exception.DuplicateInventoryNameException;
import com.mftplus.mftonlinestoreee.inventories.exception.InventoryNotEmptyException;
import com.mftplus.mftonlinestoreee.inventories.mapper.InventoryMapper;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryItemRepository;
import com.mftplus.mftonlinestoreee.inventories.repository.InventoryRepository;
import com.mftplus.mftonlinestoreee.inventories.service.InventoryService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Loggable
public class InventoryServiceImpl implements InventoryService {
    @Inject
    private InventoryRepository repository;
    @Inject
    private InventoryItemRepository itemRepository;
    @Inject
    private InventoryMapper mapper;
    
    @Transactional
    @Override
    public InventoryDto create(InventoryRequest request) {
        checkNameIsFree(request.name(), null);
        Inventory inventory = mapper.toEntity(request);
        return mapper.toDto(repository.save(inventory));
    }

    @Override
    public Optional<InventoryDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<InventoryDto> findAll() {return mapper.toDtoList(repository.findAll());}

    @Override
    public PageResult<InventoryDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<InventoryDto> update(Long id, InventoryRequest request) {
        return repository.findById(id)
                .map(inventory -> {
                    checkNameIsFree(request.name(), id);
                    mapper.updateEntity(request, inventory);
                    return mapper.toDto(inventory);
                });
    }

    @Transactional
    @Override
    public Optional<InventoryDto> deleteById(Long id) {
        checkIsEmpty(id);
        return repository.findById(id)
                .map(inventory -> {
                    repository.delete(inventory);
                    return mapper.toDto(inventory);
                });
    }

    private void checkNameIsFree(String name, Long currentId) {

        repository.findByName(name)
                .ifPresent(existing -> {

                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new DuplicateInventoryNameException(
                                "Inventory name already exists: " + name
                        );
                    }
                });
    }

    private void checkIsEmpty(Long inventoryId) {
        if (itemRepository.countByInventory(inventoryId) > 0) {
            throw new InventoryNotEmptyException(
                    "Inventory cannot be deleted because it contains items: " + inventoryId
            );
        }
    }}