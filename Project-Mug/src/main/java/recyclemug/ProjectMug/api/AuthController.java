package recyclemug.ProjectMug.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import recyclemug.ProjectMug.data.HeaderJwtDTO;
import recyclemug.ProjectMug.data.ResponseProfileDTO;
import recyclemug.ProjectMug.domain.cup.CustomerOrder;
import recyclemug.ProjectMug.domain.user.Customer;
import recyclemug.ProjectMug.domain.user.Partner;
import recyclemug.ProjectMug.domain.user.User;
import recyclemug.ProjectMug.dto.LoginDto;
import recyclemug.ProjectMug.dto.TokenDto;
import recyclemug.ProjectMug.jwt.JwtFilter;
import recyclemug.ProjectMug.jwt.TokenAuthenticationProvider;
import recyclemug.ProjectMug.repository.AdminRepository;
import recyclemug.ProjectMug.repository.CustomerOrderRepository;
import recyclemug.ProjectMug.repository.CustomerRepository;
import recyclemug.ProjectMug.repository.PartnerRepository;
import recyclemug.ProjectMug.service.CustomerOrderService;
import recyclemug.ProjectMug.service.UserService;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RestController
@Slf4j
public class AuthController {

    private final TokenAuthenticationProvider tokenAuthenticationProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CustomerRepository customerRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final PartnerRepository partnerRepository;
    private final Base64.Decoder decoder;
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final CustomerOrderService customerOrderService;

    @Autowired
    public AuthController(TokenAuthenticationProvider tokenAuthenticationProvider,
                          AuthenticationManagerBuilder authenticationManagerBuilder,
                          CustomerRepository customerRepository,
                          CustomerOrderRepository customerOrderRepository,
                          PartnerRepository partnerRepository,
                          AdminRepository adminRepository,
                          UserService userService,
                          CustomerOrderService customerOrderService) {
        this.tokenAuthenticationProvider = tokenAuthenticationProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.decoder = Base64.getDecoder();
        this.customerRepository = customerRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.partnerRepository = partnerRepository;
        this.adminRepository = adminRepository;
        this.userService = userService;
        this.customerOrderService = customerOrderService;
    }

    @PostMapping("/login/*")
    public ResponseEntity<TokenDto> authorizeUser(@Valid @RequestBody LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenAuthenticationProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        log.info("Login User : " + loginDto.getEmail());
        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'PARTNER', 'ADMIN')")
    public ResponseProfileDTO checkUserProfile(@RequestHeader(name = "Authorization") String token) throws IOException {
        String jwt = token.substring(7);
        String[] jwtSplit = jwt.split("\\.");
        String payload = new String(decoder.decode(jwtSplit[1]));

        // Json -> Object
        ObjectMapper mapper = new ObjectMapper();
        HeaderJwtDTO headerDTO = mapper.readValue(payload, HeaderJwtDTO.class);

        if (headerDTO.getRole().equals("ROLE_CUSTOMER")) {
            List<Customer> findByEmail = customerRepository.findByEmail(headerDTO.getEmail());
            if (!findByEmail.isEmpty()) {
                try {
                    Customer customer = findByEmail.get(0);
                    userService.updateLastLogin(customer);
                    CustomerOrder customerOrder = customerOrderRepository.findLastOrderOfCustomer(customer.getId());
                    customerOrderService.cupStateRenewal(customerOrder, customer);

                    return new ResponseProfileDTO(customer.getId(),
                            customer.getEmail(),
                            customer.getNickname(),
                            findPicture(customer.getProfilePictureAddress()),
                            headerDTO.getRole(),
                            customer.getSignupDateTIme(),
                            customer.getCustomerState(),
                            customerOrder.getReturnDateTime());
                }catch(Exception e){
                    log.error(e.toString());
                    Customer customer = findByEmail.get(0);
                    userService.updateLastLogin(customer);
                    return new ResponseProfileDTO(customer.getId(),
                            customer.getEmail(),
                            customer.getNickname(),
                            findPicture(customer.getProfilePictureAddress()),
                            headerDTO.getRole(),
                            customer.getSignupDateTIme(),
                            customer.getCustomerState(),
                            null);
                }
            }
        } else if (headerDTO.getRole().equals("ROLE_PARTNER")) {
            List<Partner> findByEmail = partnerRepository.findByEmail(headerDTO.getEmail());
            if (!findByEmail.isEmpty()) {
                User user = findByEmail.get(0);
                userService.updateLastLogin(user);
                return new ResponseProfileDTO(user.getId(),
                        user.getEmail(),
                        user.getNickname(),
                        findPicture(user.getProfilePictureAddress()),
                        headerDTO.getRole(),
                        user.getSignupDateTIme(),
                        null,
                        null);
            }
        } else {
            log.info("admin 호출");
            User user = adminRepository.findByEmail(headerDTO.getEmail());
            userService.updateLastLogin(user);
            return new ResponseProfileDTO(user.getId(),
                    user.getEmail(),
                    user.getNickname(),
                    findPicture("/home/ubuntu/images/users/default_user.jpg"),
                    headerDTO.getRole(),
                    null,
                    null
            ,null);
        }
        return null;
    }

    public byte[] findPicture(String profilePictureAddress) throws IOException {
        FileInputStream imageStream = new FileInputStream(profilePictureAddress);
        byte[] image = imageStream.readAllBytes();
        imageStream.close();
        return image;
    }
}
