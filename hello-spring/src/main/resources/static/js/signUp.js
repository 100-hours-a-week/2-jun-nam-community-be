document.addEventListener("DOMContentLoaded", () => {
  const email = document.getElementById("email");
  const password = document.getElementById("password");
  const passwordReenter = document.getElementById("password-reenter");
  const nickname = document.getElementById("nickname");
  const signupbutton = document.getElementById("signupbutton");

  function isNicknameValid(nickname) {
    const trimmedNickname = nickname.trim();
    return (
      trimmedNickname.length > 0 &&
      trimmedNickname.length <= 10 &&
      !trimmedNickname.includes(" ")
    );
  }

  function validateInputs() {
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passPattern =
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

    const email = document.getElementById("email");
    const password = document.getElementById("password");

    const isEmailValid = emailPattern.test(email.value);
    const isPasswordValid = passPattern.test(password.value);
    const isPasswordRenterValid = password.value === passwordReenter.value;
    const isNicknameInputValid = isNicknameValid(nickname.value);

    if (
      isEmailValid &&
      isPasswordValid &&
      isPasswordRenterValid &&
      isNicknameInputValid
    ) {
      signupbutton.style.backgroundColor = "#7F6AEE";
      console.log("valid");
    } else {
      signupbutton.style.backgroundColor = "#ACA0EB";
      console.log("invalid");
    }
  }

  email.addEventListener("input", validateInputs);
  password.addEventListener("input", validateInputs);
  passwordReenter.addEventListener("input", validateInputs);
  nickname.addEventListener("input", validateInputs);

  signupbutton.addEventListener("click", async (e) => {
    e.preventDefault();
    const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const passPattern =
      /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;
    const email = document.getElementById("email");
    const password = document.getElementById("password");

    const isEmailValid = emailPattern.test(email.value);
    const emailErrorMessage = document.getElementById("email-error-message");
    if (!emailPattern.test(email.value)) {
      emailErrorMessage.innerText =
        "*올바른 이메일 주소 형식을 입력해주세요 (예: example@example.com)";
      return;
    } else {
      emailErrorMessage.innerHTML = "&nbsp;";
    }

    const isPasswordValid = passPattern.test(password.value);
    const passwordErrorMessage = document.getElementById(
      "password-error-message"
    );
    let passwordLength = password.value.length;

    if (passwordLength < 8 || passwordLength > 20) {
      passwordErrorMessage.innerText =
        "*비밀번호는 8자 이상, 20자 이하여야 합니다";
      return;
    }

    if (!isPasswordValid) {
      passwordErrorMessage.innerText =
        "*대문자, 소문자, 숫자, 특수문자를 각 최소 1개 포함해야 합니다";
      return;
    } else {
      passwordErrorMessage.innerHTML = "&nbsp;";
    }

    const isPasswordRenterValid = password.value === passwordReenter.value;
    const passwordReenterErrorMessage = document.getElementById(
      "password-reenter-error-message"
    );
    if (!isPasswordRenterValid) {
      passwordReenterErrorMessage.innerText = "*비밀번호가 다릅니다";
      return;
    } else {
      passwordErrorMessage.innerHTML = "&nbsp;";
    }

    const isNicknameInputValid = isNicknameValid(nickname.value);
    const nicknameErrorMessage = document.getElementById(
      "nicknamer-error-message"
    );
    if (!isNicknameInputValid) {
      nicknameErrorMessage.innerText = "*유효한 닉네임을 입력하세요";
      return;
    } else {
      passwordErrorMessage.innerHTML = "&nbsp;";
    }

    if (rgbToHex(signupbutton.style.backgroundColor) == "#7f6aee") {
        //과제6: Fetch API를 활용해 서버와의 통신, 로그인을 위한 유저 정보가 유효한지 판단
        /* 
            현 과제의 경우 Spring Boot의 기본 포트 설정인 8080포트에서 진행
        */
        //System.out.println("before try----------------");
        try{
            const response = await fetch("http://localhost:8080/users", {
              method: "POST",
              headers: {
                  "Content-Type": "application/json"
              },
              body: JSON.stringify({
                  email: email.value,
                  password: password.value,
                  nickname: nickname.value,
              })
          });

            const message = await response.text();

            //fetch API를 통해 response를 받아오지 못하는 경우, 서버와의 연결에 문제가 있음을 암시
            if(!response.ok){
                console.log(message);
                emailErrorMessage.innerText="*이미 사용 중인 이메일입니다";
            }
            else{
                alert("회원가입이 완료되었습니다!");
                location.href="/";
            }
//            //response를 JSON 형태의 파일로 변환
//            const users = await response.json();
//
//            const user = users.some(user => user.email === email.value);
//
//            if(user){
//                emailErrorMessage.innerText="*이미 사용 중인 이메일입니다";
//            }
//            else{
//                emailErrorMessage.innerHTML = "&nbsp;";
//
//                const newUser = {
//                    email: email.value,
//                    password: password.value,
//                    nickname: nickname.value,
//                  };
//
//                //서버에 POST요청을 보내 신규 유저 등록 요청
//                const createUserResponse = await fetch('/users', {
//                    method: 'POST',
//                    headers: {
//                        'Content-Type': 'application/json'
//                    },
//                    body: JSON.stringify(newUser)
//                });
//
//                if (!createUserResponse.ok) {
//                    throw new Error("회원가입 처리 중 오류 발생");
//                }
//
//                window.location.href = 'login.html';
//            }
        }
        catch(error){
            alert("회원가입 중 문제가 발생했습니다");
            return;
        }
    }
  });
});

function rgbToHex(rgb) {
  const rgbValues = rgb.match(/\d+/g).map(Number);
  return `#${rgbValues.map((x) => x.toString(16).padStart(2, "0")).join("")}`;
}
