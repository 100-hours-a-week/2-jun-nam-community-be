document.addEventListener('DOMContentLoaded', async () => {
    const elements = getDomElements();
    const userInfo = await fetchUserInfo();
    setupValidationHandlers(elements);
    setupModifyPasswordHandler(userInfo, elements);
    setupDropdownMenu(elements);
    setupProfileEdit(userInfo, elements.editProfile);
    setupPasswordEdit(userInfo, elements.changePassword);
    setupLogout(elements.logout);
});

function getDomElements() {
    return {
        modifyButton: document.getElementById('modify-button'),
        password: document.getElementById('password'),
        passwordReenter: document.getElementById('passReenter'),
        passwordErrorMessage: document.getElementById('password-error-message'),
        passwordReenterErrorMessage: document.getElementById('pass-reenter-error-message'),
        dropdown: document.getElementById("dropdown"),
        editProfile: document.getElementById("editProfile"),
        changePassword: document.getElementById("changePassword"),
        logout: document.getElementById("logout"),
        profileIcon: document.getElementById("profile-icon"),
    };
}

async function fetchUserInfo() {
    return await (await fetch("/auth/me")).json();
}

function setupValidationHandlers({ password, passwordReenter, modifyButton }) {
    const passPattern = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

    function validateInputs() {
        const isPasswordValid = passPattern.test(password.value);
        const isMatch = passwordReenter.value === password.value;
        modifyButton.style.backgroundColor = (isPasswordValid && isMatch) ? "#7F6AEE" : "#ACA0EB";
        return isPasswordValid && isMatch;
    }

    password.addEventListener('input', validateInputs);
    passwordReenter.addEventListener('input', validateInputs);
}

function setupModifyPasswordHandler(userInfo, { modifyButton, password, passwordReenter, passwordErrorMessage, passwordReenterErrorMessage }) {
    modifyButton.addEventListener('click', async (e) => {
        e.preventDefault();

        if (!validatePassword(password.value, passwordErrorMessage)) return;
        if (password.value !== passwordReenter.value) {
            passwordReenterErrorMessage.innerText = "*비밀번호와 다릅니다.";
            return;
        } else {
            passwordReenterErrorMessage.innerHTML = "&nbsp;";
        }

        try {
            await modifyUserPassword(userInfo.id, userInfo.nickname, passwordReenter.value);
            location.href = '/index';
        } catch (error) {
            console.error("비밀번호 변경 실패:", error);
        }
    });
}

async function modifyUserPassword(userId, nickname, newPassword) {
    const response = await fetch(`http://localhost:8080/users/${userId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nickname, password: newPassword })
    });
    if (!response.ok) throw new Error('비밀번호 변경 실패');
}

function validatePassword(password, errorMessageElement) {
    const passPattern = /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;

    if (password.length < 8 || password.length > 20) {
        errorMessageElement.innerText = "*비밀번호는 8자 이상, 20자 이하여야 합니다";
        return false;
    }

    if (!passPattern.test(password)) {
        errorMessageElement.innerText = "*대문자, 소문자, 숫자, 특수문자를 각 최소 1개 포함해야 합니다";
        return false;
    }

    errorMessageElement.innerHTML = "&nbsp;";
    return true;
}

function setupProfileEdit(userInfo, editProfile){
    editProfile.addEventListener("click", (e) => {
      e.preventDefault();
      console.log(userInfo.id + ' ' + editProfile);
      location.href = `/users/${userInfo.id}/edit`;
  });
  }
  
  function setupPasswordEdit(userInfo, changePassword){
    changePassword.addEventListener("click", (e) => {
      e.preventDefault();
      location.href = `/users/${userInfo.id}/password`;
  });
  }
  
  async function setupLogout(logout){
    logout.addEventListener("click", async (e) => {
      e.preventDefault();
      try {
        const response = await fetch("/auth/logout", { method: "POST" });
  
        if (response.ok) {
            alert("로그아웃 되었습니다.");
            location.href = "/"; // 로그인 페이지로 이동
        } else {
            alert("로그아웃에 실패했습니다.");
        }
    } catch (error) {
        console.error("❌ 로그아웃 중 오류 발생:", error);
    }
  });
  }
  
  function setupDropdownMenu({ profileIcon, dropdown }){
    profileIcon.addEventListener('click', (e) => {
      e.stopPropagation();
      dropdown.classList.toggle("show");
    });
      
    window.addEventListener("click", () => {
      dropdown.classList.remove("show");
    });
  }