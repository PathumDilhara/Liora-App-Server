package org.appvibessolution.user.service;

import jakarta.transaction.Transactional;
import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.GetUserDTO;
import org.appvibessolution.user.model.AppUser;
import org.appvibessolution.user.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {


    private final UserRepo userRepo;
    private final ModelMapper modelMapper;


    public UserService(
            UserRepo userRepo,
            ModelMapper modelMapper
            ) {
        this.userRepo = userRepo;
        this.modelMapper = modelMapper;
    }

    // ------------  User Profile Management ------------

    public List<GetUserDTO> getAllUser(){
        List<AppUser> users = userRepo.findAll();
        return  modelMapper.map(users, new TypeToken<List<GetUserDTO> >(){}.getType());
    }

    public GetUserDTO getPublicUserById(String userId){
        AppUser user = userRepo.findById(userId).orElse(null);
        return  modelMapper.map(user, new TypeToken<GetUserDTO>(){}.getType());
    }

    public List<CreateUserDTO> searchUsers(String searchTerm) {
        List<AppUser> users = userRepo.searchUsers(searchTerm);
        return modelMapper.map(users, new TypeToken<List<CreateUserDTO>>(){}.getType());
    }

    public CreateUserDTO getCurrentUser(String userId){
        AppUser user = userRepo.findById(userId).orElse(null);
        return modelMapper.map(user, new TypeToken<CreateUserDTO>(){}.getType());
    }

    public String updateUser(CreateUserDTO userDTO){
        return userRepo.save(modelMapper.map(userDTO, AppUser.class)).toString();
    }

    public Boolean deleteUser(String userId){
        userRepo.deleteById(userId);
        return true;
    }

    // ------------ User Search / Discovery ------------

    public List<CreateUserDTO> getRandomUser() {
        List<AppUser> userList = userRepo.findAll();
        return  modelMapper.map(userList, new TypeToken<List<CreateUserDTO>>(){}.getType());
    }
}
