// validation.js

function validateEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function passwordsMatch(password, confirmPassword) {
    return password === confirmPassword;
}
