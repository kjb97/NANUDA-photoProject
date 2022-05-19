function userLogin(){
  var userId = document.getElementById("userId").value;
  var userPw = document.getElementById("userPw").value;

  if(userId == ""){
    alert("아이디 입력해주세요");
    return false;
  };
  if(userPw == ""){
    alert("비밀번호 입력해주세요");
    return false;
  };
  if(userId == "kjh" && userPw == "1234"){
    alert("환영합니다");
  }else{
    alert("계정 정보가 다릅니다")
  }
}