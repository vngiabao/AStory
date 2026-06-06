// package com.hcmc.itc.cdsservice.services;

// import com.hcmc.itc.cdsservice.models.SysNguoiDung;
// import com.hcmc.itc.cdsservice.repositories.SysNguoiDungRepository;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.validator.routines.EmailValidator;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import java.util.regex.Pattern;

// /**
// * @author {thuanpk, quyenhvt, thuannp, hoan2nt, dan2nqt}@hcmpc.com.vn
// */
// @Slf4j
// @Service(value = "userService")
// public class UserService implements UserDetailsService {
// private final SysNguoiDungRepository userRepository;

// @Autowired
// public UserService(SysNguoiDungRepository userRepository) {
// this.userRepository = userRepository;
// }

// @Override
// public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

// SysNguoiDung user = userRepository.findByTenTaiKhoanIgnoreCase(username);

// if (EmailValidator.getInstance().isValid(username)) {
// user = userRepository.findByDiaChiEmailIgnoreCase(username);
// log.info("UserService -> loadUserByUsername: {}", user);
// }

// if (user == null) {
// throw new UsernameNotFoundException("Incorrect Username / Password supplied!");
// }

// username = user.getTenTaiKhoan();

// return org.springframework.security.core.userdetails.User
// .withUsername(username)
// .password(user.getMatKhau())
// .authorities(user.getRoles())
// .accountExpired(false)
// .accountLocked(false)
// .credentialsExpired(false)
// .disabled(false)
// .build();
// }

// private boolean isEmailValid(String emailAddress) {
// return Pattern.compile("^(.+)@(\\S+)$")
// .matcher(emailAddress)
// .matches();
// }
// }
