// Wait for the DOM to fully load
document.addEventListener('DOMContentLoaded', function () {
    const resetPasswordForm = document.getElementById('resetPasswordForm');
    const newPasswordInput = document.getElementById('newPassword');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const submitButton = document.getElementById('submitButton');
    const errorMessage = document.getElementById('errorMessage');

    // URL parameters (userId and resetToken) would be parsed from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');
    const resetToken = urlParams.get('token');

    // Validate if both userId and token are present in the URL
    if (!userId || !resetToken) {
        errorMessage.textContent = "Invalid or missing user ID or reset token.";
        errorMessage.style.display = 'block';
        return;
    }

    // Form submission event handler
    resetPasswordForm.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent form from submitting the usual way

        const newPassword = newPasswordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        // Basic validation for password fields
        if (!newPassword || !confirmPassword) {
            errorMessage.textContent = "Both password fields are required.";
            errorMessage.style.display = 'block';
            return;
        }

        if (newPassword !== confirmPassword) {
            errorMessage.textContent = "Passwords do not match.";
            errorMessage.style.display = 'block';
            return;
        }

        // Call the reset password API
        resetPassword(userId, resetToken, newPassword);
    });

    // Function to send the reset password request to the backend API
    function resetPassword(userId, resetToken, newPassword) {
        // Disable the submit button while processing
        submitButton.disabled = true;

        // Prepare the payload to send to the API
        const payload = {
            userId: userId,
            resetToken: resetToken,
            newPassword: newPassword
        };

        // Make the API call to reset the password
        fetch('/api/v1/reset-password', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // If the password was successfully reset, show a success message
                    window.location.href = '/password-reset-success';  // Redirect to a success page
                } else {
                    // If there was an error, show the error message
                    errorMessage.textContent = data.message || "An error occurred while resetting your password.";
                    errorMessage.style.display = 'block';
                }
            })
            .catch(error => {
                // Handle any network or other errors
                errorMessage.textContent = "Network error: Unable to reset your password.";
                errorMessage.style.display = 'block';
            })
            .finally(() => {
                // Re-enable the submit button after the process
                submitButton.disabled = false;
            });
    }
});
