package com.mftplus.mftonlinestoreee.users.service.impl;

import com.mftplus.mftonlinestoreee.core.dto.PageResult;
import com.mftplus.mftonlinestoreee.core.security.BCryptPasswordHash;
import com.mftplus.mftonlinestoreee.users.dto.ProfileDto;
import com.mftplus.mftonlinestoreee.users.dto.ProfileRequest;
import com.mftplus.mftonlinestoreee.users.dto.UserDto;
import com.mftplus.mftonlinestoreee.users.dto.UserRequest;
import com.mftplus.mftonlinestoreee.users.entity.Profile;
import com.mftplus.mftonlinestoreee.users.entity.Role;
import com.mftplus.mftonlinestoreee.users.entity.User;
import com.mftplus.mftonlinestoreee.users.exception.DuplicateUsernameException;
import com.mftplus.mftonlinestoreee.users.exception.RoleNotFoundException;
import com.mftplus.mftonlinestoreee.users.mapper.ProfileMapper;
import com.mftplus.mftonlinestoreee.users.mapper.UserMapper;
import com.mftplus.mftonlinestoreee.users.repository.ProfileRepository;
import com.mftplus.mftonlinestoreee.users.repository.RoleRepository;
import com.mftplus.mftonlinestoreee.users.repository.UserRepository;
import com.mftplus.mftonlinestoreee.users.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class UserServiceImpl implements UserService {
    @Inject
    private UserRepository repository;
    @Inject
    private RoleRepository roleRepository;
    @Inject
    private ProfileRepository profileRepository;
    @Inject
    private UserMapper mapper;
    @Inject
    private ProfileMapper profileMapper;
    @Inject
    private BCryptPasswordHash passwordHash;

    @Transactional
    @Override
    public UserDto create(UserRequest request) {
        checkUsernameIsFree(request, null);
        User user = mapper.toEntity(request);
        user.setPassword(hash(request.password()));
        assignRoles(user, request.roleIds());
        return mapper.toDto(repository.save(user));
    }

    @Transactional
    @Override
    public Optional<UserDto> findById(Long id) {
        return repository.findById(id).map(mapper::toDto);
    }

    @Transactional
    @Override
    public List<UserDto> findAll() {
        return mapper.toDtoList(repository.findAll());
    }

    @Override
    public PageResult<UserDto> findAll(int page, int size) {
        return repository.findPage(page, size).map(mapper::toDto);
    }

    @Transactional
    @Override
    public Optional<UserDto> update(Long id, UserRequest request) {
        return repository.findById(id)
                .map(user -> {
                    checkUsernameIsFree(request, id);
                    mapper.updateEntity(request, user);
                    user.setPassword(hash(request.password()));
                    assignRoles(user, request.roleIds());
                    return mapper.toDto(user);
                });
    }

    @Transactional
    @Override
    public Optional<UserDto> deleteById(Long id) {
        return repository.findById(id)
                .map(user -> {
                    repository.delete(user);
                    return mapper.toDto(user);
                });
    }

    private void checkUsernameIsFree(UserRequest request, Long currentId) {
        repository.findByUsername(request.username())
                .filter(existing -> !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new DuplicateUsernameException(request.username());
                });
    }

    private void assignRoles(User user, Set<Long> roleIds) {
        user.getRoles().clear();
        if (roleIds == null) {
            return;
        }
        for (Long roleId : roleIds) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RoleNotFoundException(roleId));
            user.getRoles().add(role);
        }
    }

    private String hash(String password) {
        return passwordHash.generate(password.toCharArray());
    }

    private Set<Role> loadRoles( Set<Long> roleIds){
        if(roleIds == null || roleIds.isEmpty()){
            return new HashSet<>();
        }
        Set<Role> roles= new HashSet<>();
        for (Long roleId : roleIds) {
            Role role= roleRepository.findById(roleId)
                    .orElseThrow(()-> new RoleNotFoundException(roleId));
            roles.add(role);
        }
        return roles;
    }

    @Override
    public Optional<ProfileDto> findProfile(Long userId) {
        return profileRepository.findByUser(userId).map(profileMapper::toDto);
    }

    @Transactional
    @Override
    public Optional<ProfileDto> saveProfile(Long userId, ProfileRequest request) {
        return repository.findById(userId)
                .map(user -> {
                    Profile profile= profileRepository
                            .findByUser(userId)
                            .orElseGet(()-> {
                                Profile newProfile=new Profile();
                                newProfile.setUser(user);
                                return newProfile;
                            });
                    profileMapper.updateEntity(request, profile);

                    Profile savedProfile = profileRepository.save(profile);

                    return profileMapper.toDto(savedProfile);
                });
    }
}
