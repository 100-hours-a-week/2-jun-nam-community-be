document.addEventListener('DOMContentLoaded', async () => {
    const elements = getDomElements();
    console.log("postid : " + elements.postId);
    const userInfo = await fetchUserInfo();
    const postInfo = await fetchPostInfo(elements.postId);
    setupProfileEdit(userInfo, elements.editProfile, elements.profileIcon);
    setupPasswordEdit(userInfo, elements.changePassword);
    setupLogout(elements.logout);
    setupDropdownMenu(elements);
    setupValidationHandlers(postInfo, elements);
    setupPostEditHandler(userInfo, elements);
});

function getDomElements() {
    return {
        postId: window.location.pathname.match(/\/posts\/(\d+)\/edit/)[1],
        dropdown: document.getElementById("dropdown"),
        editProfile: document.getElementById("editProfile"),
        changePassword: document.getElementById("changePassword"),
        logout: document.getElementById("logout"),
        profileIcon: document.getElementById("profile-icon"),
        editBtn: document.getElementById('modify-post-button'),
        postTitle: document.getElementById('edit-post-form-title'),
        postInput: document.getElementById('edit-post-body-text-input'),
        editForm: document.getElementById('edit-post-form')
    };
}

async function fetchUserInfo() {
    const user = await (await fetch("http://localhost:8080/auth/me")).json();
    console.log(user);
    return user;
}

async function fetchPostInfo(postId) {
    const post = await (await fetch(`http://localhost:8080/api/posts/${postId}`)).json();
    return post;
}

function setupProfileEdit(userInfo, editProfile, profileIcon){
    profileIcon.src = userInfo.profileImage;
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

function setupValidationHandlers(post, { postTitle, postInput, editBtn }) {
    function validateInputs() {
        editBtn.style.backgroundColor = (postTitle.value.trim() && postInput.value.trim()) ? "#7F6AEE" : "#ACA0EB";
    }
    postTitle.value = post.title;
    postInput.value = post.content;
    postTitle.addEventListener('input', validateInputs);
    postInput.addEventListener('input', validateInputs);
}

function setupPostEditHandler(userInfo, { postId, editForm, postTitle, postInput }) {
    editForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        console.log("debug setpostedithandler :");
        try {
            const redirectURL = await editPost(postId, userInfo, postTitle.value, postInput.value);
            location.href = redirectURL;
        } catch (error) {
            console.error('네트워크 오류:', error);
        }
    });
}

async function editPost(postId, userInfo, title, content) {
    const response = await fetch(`http://localhost:8080/posts/${postId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify({
            title,
            content,
        })
    });
    if (!response.ok) throw new Error('네트워크에 문제가 발생했습니다');
    return response.headers.get("Location");
}
