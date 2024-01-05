// console.log("hello");

  $(document).ready(function() {
    setTimeout(function() {
      $('#myerror').alert('close');
    }, 3000); 
  });

  // const toggleSidebar = () => {
  //   $(".sidebar").css("display","block");
  //   $(".content").css("margin-left","20%")

  //   if ($(".sidebar").is(":visible")) {

  //     $(".sidebar").css("display","none");
  //     $(".content").css("margin-left","0%")
  //     $("#btn").css(crossBtn);
  //     $("#btn").html("&times;");


  //   }else{

  //     $(".sidebar").css("display","block");
  //     $(".content").css("margin-left","20%")

  //   }
  // }

  const toggleSidebar = () => {
    const sidebar = $(".sidebar");
    const content = $(".content");

    sidebar.toggleClass("active");
    content.toggleClass("active");

    if (sidebar.hasClass("active")) {
      $("#btn").toggle();
      $("#btn").html("&times;");
    } else {
      
      $("#btn").html("&#9776;");
    }
  }

  function validateForm() {
    // Validate your form fields
    var name = document.getElementById("name").value;
    var secondname = document.getElementById("secondname").value;
    var phone = document.getElementById("phone").value;
    var workemail = document.getElementById("workemail").value;
    var work = document.getElementById("work").value;
    var image = document.getElementById("image").value;
    var description = document.getElementById("description").value;

    // Example validation (you can add more specific validation)
    if (!name || !secondname || !phone || !workemail || !work || !image || !description) {
        // Display an error message using SweetAlert
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            text: 'Please fill in all fields!',
        });
        return false; // Prevent form submission
    }

    // Additional validation logic if needed

    // If all validations pass, return true to allow form submission
    return true;
}
console.log("hello");


function deleteContact(cid){
  swal({
    title: "Are you sure?",
    text: "You want to delete this contact...?",
    icon: "warning",
    buttons: true,
    dangerMode: true,
  })
  .then((willDelete) => {
    if (willDelete) {
      window.location = "/user/delete/" + cid;
    } else {
      swal("Your Contact is safe !!");
    }
  });
}

function displayImage(input) {
  var preview = document.getElementById('preview');
  var imagePreview = document.getElementById('imagePreview');

  if (input.files && input.files[0]) {
      var reader = new FileReader();

      reader.onload = function (e) {
          preview.src = e.target.result;
          imagePreview.style.display = 'block';
      };

      reader.readAsDataURL(input.files[0]);
  } else {
      preview.src = '#';
      imagePreview.style.display = 'none';
  }
}