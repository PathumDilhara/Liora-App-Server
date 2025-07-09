package org.appvibessolution.user.service;

import jakarta.transaction.Transactional;
import org.appvibessolution.user.dto.CreateUserDTO;
import org.appvibessolution.user.dto.GetUserDTO;
import org.appvibessolution.user.dto.LoginUserDTO;
import org.appvibessolution.user.model.User;
import org.appvibessolution.user.repo.UserRepo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ModelMapper modelMapper;

    // ------------  User Profile Management ------------

    public List<GetUserDTO> getAllUser(){
        List<User> users = userRepo.findAll();
        return  modelMapper.map(users, new TypeToken<List<GetUserDTO> >(){}.getType());
    }

    public GetUserDTO getPublicUserById(String userId){
        User user = userRepo.findById(userId).orElse(null);
        return  modelMapper.map(user, new TypeToken<GetUserDTO>(){}.getType());
    }

    public List<CreateUserDTO> searchUsers(String searchTerm) {
        List<User> users = userRepo.searchUsers(searchTerm);
        return modelMapper.map(users, new TypeToken<List<CreateUserDTO>>(){}.getType());
    }

    public CreateUserDTO getCurrentUser(String userId){
        User user = userRepo.findById(userId).orElse(null);
        return modelMapper.map(user, new TypeToken<CreateUserDTO>(){}.getType());
    }

    public String updateUser(CreateUserDTO userDTO){
        return userRepo.save(modelMapper.map(userDTO, User.class)).toString();
    }

    public Boolean deleteUser(String userId){
        userRepo.deleteById(userId);
        return true;
    }

    // ------------  Authentication (Login & Register) ------------

    public CreateUserDTO createUser(CreateUserDTO userDTO) {
        userRepo.save(modelMapper.map(userDTO, User.class));
        return userDTO;
    }

    public LoginUserDTO loginUser(LoginUserDTO loginUserDTO) {
        userRepo.findById(loginUserDTO.getEmail());
        if(loginUserDTO.getPassword().equals(searchUsers(loginUserDTO.getEmail()).get(0).getPassword())){
            return loginUserDTO;
        }
        return null;
    }

    public String logoutUser() {
        return "User logged out";
    }

    // ------------ User Search / Discovery ------------

    public List<CreateUserDTO> getRandomUser() {
        List<User> userList = userRepo.findAll();
        return  modelMapper.map(userList, new TypeToken<List<CreateUserDTO>>(){}.getType());
    }
}
