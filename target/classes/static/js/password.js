function check_pass() {
    if (document.getElementById('password').value ==
            document.getElementById('confirm_password').value) {
        document.getElementById('submit').disabled = false;
        document.getElementById('message').innerText = "Matching";
        document.getElementById('message').style.color = 'green';

    } else {
        document.getElementById('submit').disabled = true;
        document.getElementById('message').innerText = "Not matching";
        document.getElementById('message').style.color = 'red';
        
    }
}

function valider() {
    // if the fill is vide
    if((document.fsignup.username.value != "")&&(document.fsignup.email.value != "")&&(document.fsignup.password.value != "")&&(document.fsignup.birthDate.value != "")&&(document.fsignup.phone.value != "")) {
      // then submit form
      document.fsignup.submit();
      return true;
    }
    else {
      alert("fill all the blanks");
      return false;
    }
  }