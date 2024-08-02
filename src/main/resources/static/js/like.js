document.addEventListener('DOMContentLoaded', function() {
    const likeButtons = document.querySelectorAll('.like-button');

    likeButtons.forEach(button => {
        button.addEventListener('click', function() {
			console.log("****** Inside likeButtons.forEach *********");
            const productId = button.getAttribute('data-product-id');
            if (!productId) {
                console.error('Product ID not found!');
                return;
            }
            fetch(`/products/${productId}/like`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            }).then(response => {
                if (response.ok) {
                    alert('Product liked successfully!');
                } else {
                    alert('Failed to like the product.');
                }
            });
        });
    });
});

