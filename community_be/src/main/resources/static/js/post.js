// //과제 4: fetch API를 사용하기 때문에 function을 async로 수정
// document.addEventListener("DOMContentLoaded", async () => {
//   //과제4: url을 통해 특정 post의 id를 전달하도록 index.html에서 설정했음. 이를 활용해 서버에서 특정 게시글의 정보를 받아옴
//   //URLSearchParams를 통해 url을 통해 전달될 특정 값을 key-value 단위로 만들어서 관리할 수 있음
//   const postId = window.location.pathname.split("/").pop();
  
//   const editProfile = document.getElementById('editProfile');

//   const postBody = document.getElementById("posts-body");
//   const userInfo = await (await fetch("/auth/me")).json();
  
//   editProfile.addEventListener('click', () => {
//     location.href=`/users/${userInfo.id}/edit`
//   })

//   try {
//     const post = await fetchPostData(postId);

//     if (post == null) {
//       postBody.innerHTML = `<p>잘못된 접근입니다</p>`;
//       return;
//     }
//     console.log(post.comments);
//     const postComments = post.comments;

//     postBody.innerHTML =
//       `<div class="author-post-card">
//             <h2 class="post-title">${post.title}</h2>
//             <div class="post-meta">
//                 <div class="author-profile"></div>
//                 <div class="author-name">${post.author}</div>
//                 <span class="post-date">${post.createdAt}</span>
//                 <div class="buttons">
//                     <button id="modify-post">수정</button>
//                     <button id="delete-post">삭제</button>
//                 </div>
//             </div>
//         </div>
//         <hr class="divider">
//         <div class="post-text">
//             <img class="post-image" src="https://plus.unsplash.com/premium_photo-1701192799526-1a042fa6bdba?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="">
//             <div class="post-text-meta">
//                 ${post.content}
//             </div>
//         </div>
//         <div class="post-buttons">
//             <div class="post-num-likes" id="like-button">
//                 <div id="num-likes">${post.likeCount}</div>
//                 <p>좋아요수</p>
//             </div>
//             <div class="post-num-views">
//                 <p>${post.viewCount}</p>
//                 <p>조회수</p>
//             </div>
//             <div class="post-num-comments">
//                 <p>${post.commentCount}</p>
//                 <p>댓글</p>
//             </div>
//         </div>
//         <hr class="divider">
//         <div class="post-write-comment">
//             <form action="">
//                <div class="post-write-comment-body">
//                     <textarea name="" id="post-write-comment-text" placeholder="댓글을 입력해주세요!"></textarea>
//                     <button class="registerCommentButton" id="register-comment-button" disabled>댓글 등록</button>
//                </div>
//             </form>
//         </div>
//         <div id="comment-section">
//         </div>
//         ` + postBody.innerHTML;

//     //post에 comment가 있는 경우
//     const commentSection = document.getElementById("comment-section");
//     if (postComments.length != 0) {
//       postComments.forEach((comment) => {
//         console.log("id: " + comment.id);
//         const commentElement = document.createElement("div");
//         commentElement.classList.add("comment-post-card");

//         if (comment.author == userInfo.nickname) {
//           commentElement.innerHTML = `
//           <div class="post-meta">
//               <div class="author-profile"></div>
//               <div class="author-name">${comment.author}</div>
//               <span class="post-date">${new Date(
//                 comment.createdAt
//               ).toLocaleString()}</span>
//                <div class="buttons">
//                     <button class="modify-comment" id="modify-comment">수정</button>
//                     <button class="delete-comment" id="delete-comment" data-id="${
//                       comment.id
//                     }">삭제</button>
//                 </div>
//           </div>
//           <p class="post-card-text" id="postCardText">${comment.content}</p>`;

//           commentSection.appendChild(commentElement);
//         } else {
//           commentElement.innerHTML = `
//               <div class="post-meta">
//                   <div class="author-profile"></div>
//                   <div class="author-name">${comment.author}</div>
//                   <span class="post-date">${new Date(
//                     comment.createdAt
//                   ).toLocaleString()}</span>
//               </div>
//               <p class="post-card-text" id="postCardText">${
//                 comment.content
//               }</p>`;

//           commentSection.appendChild(commentElement);
//         }
//       });

//       //Modify comment EventHandler
//       const modifyCommentBtn = document.getElementById("modify-comment");
//       const postCardText = document.getElementById("postCardText").innerText;
//       modifyCommentBtn.addEventListener("click", () => {
//         commentText.value = postCardText;
//         commentText.dispatchEvent(new Event("input"));
//         registerCommentBtn.textContent = "댓글 수정";
//       });

//       //Delete comment EventHandler
//       //과제 4 추가 : DELETE 요청을 통한 comment 삭제, 기존에는 getElementById를 통해 button을 가져왔지만 특정 유저가
//       //댓글을 2개 이상 다는 경우, getElementById는 최우선으로 나오는 comment에만 작동하게 되므로 querySelector를 이용한
//       //방식으로 교체. querySelector의 경우, class 이름을 기준으로 찾으므로 수정, 삭제 버튼에 클래스 이름 추가
//       const commentModalOverlay = document.getElementById(
//         "commentModalOverlay"
//       );

//       //model에서 삭제해야 할 댓글의 아이디를 기억하기 위해 사용하는 임시 변수.
//       let commentModalId;

//       console.log(document.querySelectorAll(".delete-comment"));
//       document.querySelectorAll(".delete-comment").forEach((button) => {
//         button.addEventListener("click", (e) => {
//           if (e.target.classList.contains("delete-comment")) {
//             e.preventDefault();
//             let commentId = button.getAttribute("data-id");
//             commentModalId = commentId;
//             commentModalOverlay.style.display = "flex";
//             document.body.style.overflow = "hidden";
//           }
//         });
//       });

//       const commentCancelButton = document.getElementById(
//         "commentCancelButton"
//       );
//       const commentConfirmButton = document.getElementById(
//         "commentConfirmButton"
//       );

//       commentCancelButton.addEventListener("click", () => {
//         commentModalOverlay.style.display = "none";
//         document.body.style.overflow = "auto";
//       });

//       commentConfirmButton.addEventListener("click", async (e) => {
//         e.preventDefault();
//         const deleteCommentResponse = await fetch(
//           `http://localhost:8080/comments/${commentModalId}`,
//           {
//             method: "DELETE",
//           }
//         );

//         if (!deleteCommentResponse.ok) {
//           alert("댓글 삭제에 실패했습니다.");
//           return;
//         }
//         commentModalOverlay.style.display = "none";
//         document.body.style.overflow = "auto";
//         alert("댓글이 성공적으로 삭제되었습니다!");
//         location.reload();
//       });
//     }
//     else{
//       commentSection.innerHTML = "<p> 댓글이 존재하지 않습니다. </p>"
//     }
//   } catch (error) {}

//   //Like EventHandler
//   const likeButton = document.getElementById("like-button");
//   likeButton.addEventListener("click", () => {
//     const numLikes = document.getElementById("num-likes");
//     const element = document.getElementsByClassName("post-num-likes")[0];
//     const style = getComputedStyle(element);
//     const bgColor = style["background-color"];

//     if (rgbToHex(bgColor) == "#d9d9d9") {
//       element.style.backgroundColor = "#ACA0EB";
//       console.log(numLikes.innerText);
//       numLikes.innerText = Number(numLikes.innerText) + 1;
//     } else if (rgbToHex(bgColor) == "#aca0eb") {
//       element.style.backgroundColor = "#d9d9d9";
//       numLikes.innerText = Number(numLikes.innerText) - 1;
//     }
//   });

//   //CommentText Change EventHandler
//   const registerCommentBtn = document.getElementById("register-comment-button");
//   const commentText = document.getElementById("post-write-comment-text");
//   commentText.addEventListener("input", () => {
//     const content = commentText.value.trim();

//     if (content.length == 0) {
//       registerCommentBtn.disabled = true;
//       registerCommentBtn.style.backgroundColor = "#aca0eb";
//     } else {
//       registerCommentBtn.disabled = false;
//       registerCommentBtn.style.backgroundColor = "#7f6aee";
//     }
//   });

//   //header profile icon handelr
//   const dropdown = document.getElementById("dropdown");
//   const profileIcon = document.getElementById("profile-icon");

//   profileIcon.addEventListener("click", (e) => {
//     e.stopPropagation();
//     dropdown.classList.toggle("show");
//   });

//   window.addEventListener("click", () => {
//     dropdown.classList.remove("show");
//   });

//   //Modify post EventHandler
//   const modifyPostBtn = document.getElementById("modify-post");
//   modifyPostBtn.addEventListener("click", () => {
//     location.href = `/posts/${postId}/edit`;
//   });

//   //Delete post button Eventhandler
//   const deletePostBtn = document.getElementById("delete-post");
//   const postModalOverlay = document.getElementById("postModalOverlay");
//   deletePostBtn.addEventListener("click", (e) => {
//     e.preventDefault();
//     postModalOverlay.style.display = "flex";
//     document.body.style.overflow = "hidden";
//   });

//   //Comment Button Click EventHandler
//   registerCommentBtn.addEventListener("click", async (e) => {
//     e.preventDefault();
//     console.log(postId);
//     if (registerCommentBtn.textContent === "댓글 수정") {
//       document.getElementById("postCardText").innerHTML = commentText.value;
//       registerCommentBtn.textContent = "댓글 등록";
//     }

//     //과제4: 특정 포스트에 댓글 추가하기
//     const newComment = {
//       author: userInfo.nickname,
//       content: commentText.value,
//       onPostId: postId,
//     };

//     try {
//       const createCommentResponse = await fetch(
//         `http://localhost:8080/comments`,
//         {
//           method: "POST",
//           headers: {
//             "Content-Type": "application/json",
//           },
//           body: JSON.stringify(newComment),
//         }
//       );

//       if (!createCommentResponse.ok) {
//         throw new Error("댓글 작성에 실패했습니다.");
//       }

//       alert("댓글이 성공적으로 작성되었습니다!");

//       commentText.value = "";
//       commentText.dispatchEvent(new Event("input"));

//       location.reload();
//     } catch (error) {
//       console.error("댓글 작성 중 오류 발생:", error);
//     }
//   });

//   //post modal EventHandler
//   const postCancelButton = document.getElementById("postCancelButton");
//   const postConfirmButton = document.getElementById("postConfirmButton");

//   postCancelButton.addEventListener("click", () => {
//     postModalOverlay.style.display = "none";
//     document.body.style.overflow = "auto";
//   });

//   //과제 4 추가: DELETE 요청을 통해 게시글 삭제 버튼 클릭시 게시글을 목록에서 삭제 후 index.html에 반영
//   postConfirmButton.addEventListener("click", async (e) => {
//     e.preventDefault();

//     const deleteResponse = await fetch(`http://localhost:8080/posts/${postId}`, {
//         method: "DELETE"
//     });

//     if (!deleteResponse.ok) {
//         throw new Error("게시글 삭제 실패");
//     }

//     alert("게시글이 삭제되었습니다.");

//     postModalOverlay.style.display = "none";
//     document.body.style.overflow = "auto";
//     location.href = '/index';
//   });
// });

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
                <span class="post-date">${post.createdAt}</span>
                <div class="buttons">
                    <button id="modify-post">수정</button>
                    <button id="delete-post">삭제</button>
                </div>
            </div>
        </div>
        <hr class="divider">
        <div class="post-text">
            <img class="post-image" src="https://plus.unsplash.com/premium_photo-1701192799526-1a042fa6bdba?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="">
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
                <span class="post-date">${post.createdAt}</span>
            </div>
        </div>
        <hr class="divider">
        <div class="post-text">
            <img class="post-image" src="https://plus.unsplash.com/premium_photo-1701192799526-1a042fa6bdba?q=80&w=1974&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="">
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
            <div class="author-profile"></div>
            <div class="author-name">${comment.author}</div>
            <span class="post-date">${new Date(
              comment.createdAt
            ).toLocaleString()}</span>
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
                <div class="author-profile"></div>
                <div class="author-name">${comment.author}</div>
                <span class="post-date">${new Date(
                  comment.createdAt
                ).toLocaleString()}</span>
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
      content: commentText.value,
      onPostId: postId,
      onUserId: userInfo.id,
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