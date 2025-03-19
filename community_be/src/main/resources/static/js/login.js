document.addEventListener('DOMContentLoaded', () => {
  const elements = getDomElements();
  setupEventListeners(elements);
});

function getDomElements() {
  return {
      emailInput: document.getElementById('email'),
      passwordInput: document.getElementById('password'),
      loginButton: document.getElementById('login-button'),
      emailErrorMessage: document.getElementById('email-error-message'),
      passwordErrorMessage: document.getElementById('password-error-message'),
      signUpButton: document.getElementById('sign-up-button')
  };
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const PASSWORD_REGEX = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

function validateInputs({ emailInput, passwordInput, loginButton }) {
  const isEmailValid = EMAIL_REGEX.test(emailInput.value);
  const isPasswordValid = PASSWORD_REGEX.test(passwordInput.value);
  loginButton.style.backgroundColor = (isEmailValid && isPasswordValid) ? "#7F6AEE" : "#ACA0EB";
}

function setupEventListeners(elements) {
  elements.emailInput.addEventListener('input', () => validateInputs(elements));
  elements.passwordInput.addEventListener('input', () => validateInputs(elements));
  elements.loginButton.addEventListener('click', (e) => handleLogin(e, elements));
  elements.signUpButton.addEventListener('click', () => moveToSignUpPage() );
}

async function handleLogin(event, elements) {
  event.preventDefault();
  resetErrorMessages(elements);
  
  if (!validateForm(elements)) return;
  
  try {
    const response = processLogin(elements);

  } catch (error) {
      alert("로그인 중 문제가 발생했습니다");
  }
}

function resetErrorMessages({ emailErrorMessage, passwordErrorMessage }) {
  emailErrorMessage.innerHTML = "&nbsp;";
  passwordErrorMessage.innerHTML = "&nbsp;";
}

function validateForm({ emailInput, passwordInput, emailErrorMessage, passwordErrorMessage }) {
  if (!EMAIL_REGEX.test(emailInput.value)) {
      emailErrorMessage.textContent = "*올바른 이메일 주소 형식을 입력해주세요 (예: example@example.com)";
      return false;
  }

  if (!PASSWORD_REGEX.test(passwordInput.value)) {
      passwordErrorMessage.textContent = "*비밀번호는 8~20자이며, 대문자, 소문자, 숫자, 특수문자를 포함해야 합니다";
      return false;
  }
  return true;
}

//user 정보를 server에서 받아온 후, 재차 로그인 유저 검증을 하는 것 보다 바로 DB에 user 정보가 있는지 확인 후 login 가능/불가 여부를 판단하도록 수정.
async function processLogin({ emailInput, passwordInput, emailErrorMessage }) {
  const response = await fetch('/auth/login', {
    method: "POST",
    headers: {"CONTENT-TYPE": "application/json"},
    body: JSON.stringify({email: emailInput.value, password: passwordInput.value}),
  })

  if(!response.ok){
    alert("등록되지 않은 사용자입니다!");
  }
  else{
    location.href = response.url;
  }
}

function moveToSignUpPage(){
    fetch("/users", {
        method: "GET"
    })
    .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.text(); // JSON이 아니라면 response.json() 대신 response.text()
        })
        .then(data => {
            console.log("Server response:", data);
        })
        .catch(error => console.error("Fetch error:", error));
}