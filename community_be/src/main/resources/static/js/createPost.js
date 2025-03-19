document.addEventListener('DOMContentLoaded', async () => {
    const elements = getDomElements();
    const userInfo = await fetchUserInfo();
    console.log(userInfo);
    setupProfileEdit(userInfo, elements.editProfile);
    setupPasswordEdit(userInfo, elements.changePassword);
    setupLogout(elements.logout);
    setupDropdownMenu(elements);
    setupValidationHandlers(elements);
    setupPostCreationHandler(userInfo, elements);
});

function getDomElements() {
    return {
        dropdown: document.getElementById("dropdown"),
        editProfile: document.getElementById("editProfile"),
        changePassword: document.getElementById("changePassword"),
        logout: document.getElementById("logout"),
        profileIcon: document.getElementById("profile-icon"),
        createBtn: document.getElementById('create-post-button'),
        postTitle: document.getElementById('create-post-form-title'),
        postInput: document.getElementById('create-post-body-text-input'),
        createForm: document.getElementById('create-post-form')
    };
}

async function fetchUserInfo() {
    const user = await (await fetch("http://localhost:8080/auth/me")).json();
    console.log(user);
    return user;
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

function setupValidationHandlers({ postTitle, postInput, createBtn }) {
    function validateInputs() {
        createBtn.style.backgroundColor = (postTitle.value.trim() && postInput.value.trim()) ? "#7F6AEE" : "#ACA0EB";
    }
    postTitle.addEventListener('input', validateInputs);
    postInput.addEventListener('input', validateInputs);
}

function setupPostCreationHandler(userInfo, { createForm, postTitle, postInput }) {
    createForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        try {
            const redirectURL = await createPost(userInfo, postTitle.value, postInput.value);
            location.href = redirectURL;
        } catch (error) {
            console.error('네트워크 오류:', error);
        }
    });
}

async function createPost(userInfo, title, content) {
    const response = await fetch('http://localhost:8080/posts', {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
            title,
            content,
            author: userInfo.nickname,
            createdAt: new Date().toISOString(),
            modifiedAt: new Date().toISOString(),
            likeCount: 0,
            commentCount: 0,
            viewCount: 0,
            comments: [],
            interactions: [],
            authorId: userInfo.id,
        })
    });
    if (!response.ok) throw new Error('네트워크에 문제가 발생했습니다');
    return response.headers.get("Location");
}
