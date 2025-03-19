package hello.hello_spring.service;

import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다"));
    }

    public User findUser(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    @Transactional
    public void saveUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        try{
            userRepository.deleteById(id);
            System.out.println("유저 삭제 완료");
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 유저 정보 삭제 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            System.out.println("aasdfa-------------");
            e.printStackTrace();
            System.out.println("aasdfa-------------");
            throw new RuntimeException("예상치 못한 오류로 유저 정보 삭제에 실패했습니다.", e);
        }
    }
}
