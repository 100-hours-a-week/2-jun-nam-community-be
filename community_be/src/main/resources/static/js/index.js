document.addEventListener('DOMContentLoaded', async () => {
    const elements = getDomElements();
    setupEventListeners(elements);
    await loadPosts(elements.postList);
});

function getDomElements() {
    return {
        dropdown: document.getElementById("dropdown"),
        editProfile: document.getElementById("editProfile"),
        changePassword: document.getElementById("changePassword"),
        logout: document.getElementById("logout"),
        profileIcon: document.getElementById("profile-icon"),
        createPostButton: document.getElementById('create-post-button'),
        postList: document.getElementById('postList')
    };
}

async function setupEventListeners({ logout, editProfile, profileIcon, dropdown }) {
    try {
        const userInfo = await (await fetch("http://localhost:8080/auth/me")).json();
        console.log(userInfo);
        setupProfileEdit(userInfo, editProfile);
        setupPasswordEdit(userInfo, changePassword);
        setupLogout(logout);
        setupDropdownMenu(profileIcon, dropdown);
    } catch (error) {
        console.error("사용자 정보를 불러오는 데 실패했습니다", error);
        location.href = `/`;
    }
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
  
  function setupDropdownMenu(profileIcon, dropdown){
    profileIcon.addEventListener('click', (e) => {
      e.stopPropagation();
      dropdown.classList.toggle("show");
    });
      
    window.addEventListener("click", () => {
      dropdown.classList.remove("show");
    });
  }

async function loadPosts(postList) {
    try {
        const response = await fetch('http://localhost:8080/posts');
        if (!response.ok) throw new Error('서버와의 연결이 원활하지 않습니다');

        const posts = await response.json();
        if (posts.length === 0) {
            postList.innerHTML = "등록된 게시글이 없습니다";
            return;
        }

        postList.innerHTML = posts.map(renderPostCard).join('');
        setupPostEventListeners();
    } catch (error) {
        console.error('게시글을 불러오는 데 실패했습니다', error);
    }
}

function renderPostCard(post) {
    return `
        <div class="post-card" id="post-card" post-id="${post.id}">
            <h2 class="post-title">${post.title}</h2>
            <div class="post-meta">
                <span id="likes">좋아요 ${post.likeCount}</span>
                <span id="comments">댓글 ${post.commentCount}</span>
                <span id="views">조회수 ${post.viewCount}</span>
                <span class="post-date">${post.createdAt}</span>
            </div>
            <hr class="divider">
            <div class="post-author">
                <div class="author-profile"></div>
                <div class="author-name">${post.author}</div>
            </div>
        </div>
    `;
}

function setupPostEventListeners() {
    document.querySelectorAll('.post-card').forEach(post => {
        post.addEventListener('click', () => {
            const postId = post.getAttribute('post-id');
            location.href = `/posts/${postId}`;
        });
    });
}