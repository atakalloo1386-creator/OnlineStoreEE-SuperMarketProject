package com.mftplus.mftonlinestoreee.users.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.users.dto.RoleDto;
import com.mftplus.mftonlinestoreee.users.dto.RoleRequest;
import com.mftplus.mftonlinestoreee.users.entity.Role;
import com.mftplus.mftonlinestoreee.users.exception.DuplicateRoleNameException;
import com.mftplus.mftonlinestoreee.users.exception.RoleInUseException;
import com.mftplus.mftonlinestoreee.users.mapper.RoleMapper;
import com.mftplus.mftonlinestoreee.users.repository.RoleRepository;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import com.mftplus.mftonlinestoreee.users.service.RoleService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RoleServiceImpl implements RoleService {

    @Inject
    private RoleRepository roleRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private RoleMapper mapper;

    @Transactional
    @Override
    public RoleDto create(RoleRequest request) {
        checkNameIsFree(request,null);
        Role role= mapper.toEntity(request);
        return mapper.toDto(roleRepository.save(role));
    }

    @Override
    public Optional<RoleDto> findById(Long id) {
        return roleRepository.findById(id).map(mapper::toDto);
    }

    @Override
    public List<RoleDto> findAll() {
        return mapper.toDtoList(roleRepository.findAll());
    }

    @Override
    public PageResult<RoleDto> findAll(int page, int size) {
        return roleRepository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<RoleDto> update(Long id, RoleRequest request) {
        return roleRepository.findById(id)
                .map(role -> {
                    checkNameIsFree( request,id);
                    mapper.updateEntity(request,role);
                    return mapper.toDto(role);

                });
    }

    @Transactional
    @Override
    public Optional<RoleDto> deleteById(Long id) {
        return roleRepository.findById(id)
                .map(role->{
                    checkNotInUse(id);
                    roleRepository.delete(role);
                    return mapper.toDto(role);
                });
    }

    private void checkNameIsFree(RoleRequest request, Long currentId){
        roleRepository.findByName(request.name())
                .filter(existing->!existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateRoleNameException(request.name());
                });
    }

    private void checkNotInUse(Long id){
        if(userRepository.countByRole(id)>0){
            throw new RoleInUseException("cannot delete role because it is assignd to one or more users.");
        }
    }
}
