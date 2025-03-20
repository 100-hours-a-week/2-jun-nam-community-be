const likeBtnEnabled = "#d9d9d9";
const likeBtnDisabled = "#ACA0EB";
const btnEnabled = "";
const btnDisabled = "";

document.addEventListener("DOMContentLoaded", async () => {
  const staticElements = getDomStaticElements();
  const userInfo = await fetchUserInfo(staticElements.profileIcon);
  setupStaticEventListeners(userInfo, staticElements);

  const post = await fetchPostData(staticElements.postId);
  if (!post) {
    staticElements.postBody.innerHTML = `<p>잘못된 접근입니다</p>`;
      return;
  }
  renderPost(userInfo, post, staticElements.postBody);
  renderComments(post.comments, staticElements.commentSection, userInfo);
  let dynamicElements;
  setTimeout(() => {
    dynamicElements = getDomDynamicElements();
    console.log("🔍 dynamic elements:", dynamicElements);
    setupDynamicEventListeners(userInfo, dynamicElements);
  }, 100); 
});

async function fetchPostData(postId){
  const postResponse = await fetch(
    `http://localhost:8080/api/posts/${postId}`
  );
  const post = await postResponse.json();

  if(!postResponse.ok)
    return null;

  return post;
}

function rgbToHex(rgb) {
  const rgbValues = rgb.match(/\d+/g).map(Number);
  return `#${rgbValues.map((x) => x.toString(16).padStart(2, "0")).join("")}`;
}

function getDomStaticElements() {
  return {
    postId: window.location.pathname.split("/").pop(),
    dropdown: document.getElementById("dropdown"),
    editProfile: document.getElementById("editProfile"),
    changePassword: document.getElementById("changePassword"),
    logout: document.getElementById("logout"),
    postBody: document.getElementById("posts-body"),
    profileIcon: document.getElementById("profile-icon"),
    commentSection: document.getElementById("comment-section"),
    postCancelButton: document.getElementById("postCancelButton"),
    postConfirmButton: document.getElementById("postConfirmButton"),
    commentCancelButton: document.getElementById("commentCancelButton"),
    commentConfirmButton: document.getElementById("commentConfirmButton"),
    postModalOverlay: document.getElementById("postModalOverlay"),
    commentModalOverlay: document.getElementById("commentModalOverlay"),
  };
}

function getDomDynamicElements() {
  return {
    postId: window.location.pathname.split("/").pop(),
    likeButton: document.getElementById("like-button"),
    modifyPostBtn: document.getElementById("modify-post"),
    modifyCommentBtn: document.querySelectorAll(".modify-comment"), // 여러 개 선택
    postCardText: document.querySelectorAll(".post-card-text"), // 여러 개 선택
    deletePostBtn: document.getElementById("delete-post"),
    deleteCommentBtn: document.querySelectorAll(".delete-comment"), // 여러 개 선택
    postModalOverlay: document.getElementById("postModalOverlay"),
    commentModalOverlay: document.getElementById("commentModalOverlay"),
    registerCommentBtn: document.getElementById("register-comment-button"),
    commentText: document.getElementById("post-write-comment-text"),
  };
}

function setupStaticEventListeners(userInfo, elements){
  setupProfileEdit(userInfo, elements.editProfile);
  setupPasswordEdit(userInfo, elements.changePassword);
  setupLogout(elements.logout);
  setupDropdownMenu(elements);
  setupPostModalHandler(elements);
  setupCommentModalHandler(elements);
}

function setupDynamicEventListeners(userInfo, elements) {
  setupModals(elements);
  setupLikeButton(elements.postId, elements.likeButton);
  setupRegisterCommentHandler(elements, userInfo);
  setupCommentTextHandler(elements);
  setupModifyCommentHandler(elements);
  setupDeleteCommentHandler(elements);
  setupModifyPostHandler(elements);
  setupDeletePostHandler(elements);
}

async function fetchUserInfo(profileImage){
  const userData = await (await fetch("/auth/me")).json();
  profileImage.src = userData.profileImage;
  return userData;
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

function setupModals({postId, deletePostBtn, modifyPostBtn}){
  console.log(deletePostBtn);
  if(deletePostBtn){
    deletePostBtn.addEventListener("click", (e) => {
      e.preventDefault();
      postModalOverlay.style.display = "flex";
      document.body.style.overflow = "hidden";
});
  }

  if(modifyPostBtn){
    modifyPostBtn.addEventListener("click", () => {
      location.href = `/posts/${postId}/edit`;
    });
  }
}

function renderPost(userInfo, post, postBody){

    if(userInfo.id == post.authorId){
      postBody.innerHTML =
      `<div class="author-post-card">
            <h2 class="post-title">${post.title}</h2>
            <div class="post-meta">
                <div class="author-profile"></div>
                <div class="author-name">${post.author}</div>
                <span class="post-date">${convertDate(post.createdAt)}</span>
                <div class="buttons">
                    <button id="modify-post">수정</button>
                    <button id="delete-post">삭제</button>
                </div>
            </div>
        </div>
        <hr class="divider">
        <div class="post-text">
            <img class="post-image" src="${post.profileImage}" alt="">
            <div class="post-text-meta">
                ${post.content}
            </div>
        </div>
        <div class="post-buttons">
            <div class="post-num-likes" id="like-button">
                <div id="num-likes">${post.likeCount}</div>
                <p>좋아요수</p>
            </div>
            <div class="post-num-views">
                <p>${post.viewCount}</p>
                <p>조회수</p>
            </div>
            <div class="post-num-comments">
                <p>${post.commentCount}</p>
                <p>댓글</p>
            </div>
        </div>
        <hr class="divider">
        <div class="post-write-comment">
            <form action="">
               <div class="post-write-comment-body">
                    <textarea name="" id="post-write-comment-text" placeholder="댓글을 입력해주세요!"></textarea>
                    <button class="registerCommentButton" id="register-comment-button" disabled>댓글 등록</button>
               </div>
            </form>
        </div>
        <div id="comment-section">
        </div>
        ` + postBody.innerHTML;
    }
    else{
      postBody.innerHTML =
      `<div class="author-post-card">
            <h2 class="post-title">${post.title}</h2>
            <div class="post-meta">
                <div class="author-profile"></div>
                <div class="author-name">${post.author}</div>
                <span class="post-date">${convertDate(post.createdAt)}</span>
            </div>
        </div>
        <hr class="divider">
        <div class="post-text">
            <img class="post-image" src="${post.profileImage}" alt="">
            <div class="post-text-meta">
                ${post.content}
            </div>
        </div>
        <div class="post-buttons">
            <div class="post-num-likes" id="like-button">
                <div id="num-likes">${post.likeCount}</div>
                <p>좋아요수</p>
            </div>
            <div class="post-num-views">
                <p>${post.viewCount}</p>
                <p>조회수</p>
            </div>
            <div class="post-num-comments">
                <p>${post.commentCount}</p>
                <p>댓글</p>
            </div>
        </div>
        <hr class="divider">
        <div class="post-write-comment">
            <form action="">
               <div class="post-write-comment-body">
                    <textarea name="" id="post-write-comment-text" placeholder="댓글을 입력해주세요!"></textarea>
                    <button class="registerCommentButton" id="register-comment-button" disabled>댓글 등록</button>
               </div>
            </form>
        </div>
        <div id="comment-section">
        </div>
        ` + postBody.innerHTML;
    }
    
}

function renderComments(postComments, commentSection, userInfo){
  postComments = postComments || [];
  console.log(postComments);
  if (postComments.length != 0) {
    postComments.forEach((comment) => {
      console.log("id: " + comment.id);
      console.log(comment);
      const commentElement = document.createElement("div");
      commentElement.classList.add("comment-post-card");

      if (comment.onUserId == userInfo.id) {
        commentElement.innerHTML = `
        <div class="post-meta">
            <div class="author-profile"><img src="${comment.profileImage}" id="comment-image"></div>
            <div class="author-name">${comment.author}</div>
            <span class="post-date">${convertDate(comment.modifiedAt)}</span>
             <div class="buttons">
                  <button class="modify-comment" id="modify-comment">수정</button>
                  <button class="delete-comment" id="delete-comment" data-id="${
                    comment.id
                  }">삭제</button>
              </div>
        </div>
        <p class="post-card-text" id="postCardText">${comment.content}</p>`;

        commentSection.appendChild(commentElement);
      } else {
        commentElement.innerHTML = `
            <div class="post-meta">
                <div class="author-profile"><img src="${comment.profileImage}" id="comment-image"></div>
                <div class="author-name">${comment.author}</div>
                <span class="post-date">${convertDate(comment.modifiedAt)}</span>
            </div>
            <p class="post-card-text" id="postCardText">${
              comment.content
            }</p>`;

        commentSection.appendChild(commentElement);
      }
    });
  }
}

function setupLikeButton(postId, likeButton){
  const numLikes = document.getElementById("num-likes");
  const element = document.getElementsByClassName("post-num-likes")[0];

  likeButton.addEventListener('click', async () => {
      try{
        const response = await fetch(`/posts/${postId}/like`, {
          method: "POST",
        });
        const likeCount = await response.json();
        if(!response.ok){
          console.log("action not avaiable");
          return;
        }
        const style = getComputedStyle(element);
        const bgColor = style["background-color"];
        
        numLikes.innerText = likeCount;
        if (rgbToHex(bgColor) == '#d9d9d9') {
          element.style.backgroundColor = '#ACA0EB';
          console.log(numLikes.innerText);
        } 
        else if (rgbToHex(bgColor) == '#ACA0EB') {
          element.style.backgroundColor = '#d9d9d9';
        }
      }
      catch(e){
        throw new Error(e);
      }
    });
}

function setupRegisterCommentHandler({registerCommentBtn, commentText, postId}, userInfo){
    registerCommentBtn.addEventListener("click", async (e) => {
    e.preventDefault();
    console.log(postId);
    if (registerCommentBtn.textContent === "댓글 수정") {
      document.getElementById("postCardText").innerHTML = commentText.value;
      registerCommentBtn.textContent = "댓글 등록";
    }

    //과제4: 특정 포스트에 댓글 추가하기
    const newComment = {
      author: userInfo.nickname,
      profileImage: userInfo.profileImage,
      content: commentText.value,
      onPostId: postId,
      onUserId: userInfo.id,
      modifiedAt: new Date().toISOString(),
    };

    try {
      const createCommentResponse = await fetch(
        `http://localhost:8080/comments`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(newComment),
        }
      );
      console.log(createCommentResponse);
      if (!createCommentResponse.ok) {
        throw new Error("댓글 작성에 실패했습니다.");
      }

      alert("댓글이 성공적으로 작성되었습니다!");

      commentText.value = "";
      commentText.dispatchEvent(new Event("input"));

      location.reload();
    } catch (error) {
      console.error("댓글 작성 중 오류 발생:", error);
    }
  });
}

function setupCommentTextHandler({commentText, registerCommentBtn}){
    commentText.addEventListener("input", () => {
    const content = commentText.value.trim();

    if (content.length == 0) {
      registerCommentBtn.disabled = true;
      registerCommentBtn.style.backgroundColor = "#aca0eb";
    } else {
      registerCommentBtn.disabled = false;
      registerCommentBtn.style.backgroundColor = "#7f6aee";
    }
  });
}

function setupModifyCommentHandler({modifyCommentBtn, commentText, postCardText, registerCommentBtn}){
  modifyCommentBtn.forEach((button, index) => {
    button.addEventListener("click", () => {
      const text = postCardText[index].innerText;

      console.log(`📝 수정할 댓글 내용: ${text}`);

      commentText.value = text;
      commentText.dispatchEvent(new Event("input"));
      registerCommentBtn.textContent = "댓글 수정";
    });
  });
}

function setupModifyPostHandler({modifyPostBtn, postId}){
  if(modifyPostBtn){
    modifyPostBtn.addEventListener("click", () => {
      location.href = `/posts/${postId}/edit`;
    });
  }
}

function setupDeletePostHandler({deletePostBtn, postModalOverlay}){
  if(deletePostBtn){
    deletePostBtn.addEventListener("click", (e) => {
      e.preventDefault();
      postModalOverlay.style.display = "flex";
      document.body.style.overflow = "hidden";
    });
  }
}

function setupDeleteCommentHandler({deleteCommentBtn, commentModalOverlay}){
  deleteCommentBtn.forEach((button) => {
    button.addEventListener("click", (e) => {
      e.preventDefault();

      // ✅ 클릭된 버튼에서 `data-id` 가져오기
      const commentId = e.target.getAttribute("data-id");

      if (!commentId) {
        console.error("❌ 댓글 ID를 찾을 수 없습니다.");
        return;
      }

      console.log(`🗑 삭제할 댓글 ID: ${commentId}`);

      // ✅ 모달 오버레이에 ID 저장
      commentModalOverlay.dataset.commentId = commentId;
      commentModalOverlay.style.display = "flex";
      document.body.style.overflow = "hidden";
    });
  });
}

function setupPostModalHandler({postCancelButton, postModalOverlay, postId, postConfirmButton}){
  postCancelButton.addEventListener("click", () => {
        postModalOverlay.style.display = "none";
        document.body.style.overflow = "auto";
      });
    
      //과제 4 추가: DELETE 요청을 통해 게시글 삭제 버튼 클릭시 게시글을 목록에서 삭제 후 index.html에 반영
      postConfirmButton.addEventListener("click", async (e) => {
        e.preventDefault();
    
        const deleteResponse = await fetch(`http://localhost:8080/posts/${postId}`, {
            method: "DELETE"
        });
        
        console.log(deleteResponse.text());

        if (!deleteResponse.ok) {
            throw new Error("게시글 삭제 실패");
        }
    
        alert("게시글이 삭제되었습니다.");
    
        postModalOverlay.style.display = "none";
        document.body.style.overflow = "auto";
        location.href = '/index';
      });
}

function setupCommentModalHandler({commentCancelButton, commentModalOverlay, commentConfirmButton}){

  commentCancelButton.addEventListener("click", () => {
    commentModalOverlay.style.display = "none";
        document.body.style.overflow = "auto";
      });
    
      //과제 4 추가: DELETE 요청을 통해 게시글 삭제 버튼 클릭시 게시글을 목록에서 삭제 후 index.html에 반영
      commentConfirmButton.addEventListener("click", async (e) => {
                e.preventDefault();
                const commentId = commentModalOverlay.dataset.commentId;
                console.log(commentId);
                const deleteCommentResponse = await fetch(
                  `http://localhost:8080/comments/${commentId}`,
                  {
                    method: "DELETE",
                  }
                );
        
                if (!deleteCommentResponse.ok) {
                  alert("댓글 삭제에 실패했습니다.");
                  return;
                }
                commentModalOverlay.style.display = "none";
                document.body.style.overflow = "auto";
                alert("댓글이 성공적으로 삭제되었습니다!");
                location.reload();
              });
}

function convertDate(serverDate){
  const date = new Date(serverDate);
  const formattedDate = date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
      hour12: false // 24시간 형식
  });
  formattedDate.replace(/. /g, '-').replace('.', '');
  console.log("formatted date: " + formattedDate);
  return formattedDate;
}