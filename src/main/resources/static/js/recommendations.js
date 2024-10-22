$(document).ready(function() {
    // Placeholder for user ID retrieval logic
    const userId = 1; // Replace this with the actual logic to get the current user ID

    $.ajax({
        url: `/recommendations/${userId}`,
        method: 'GET',
        success: function(data) {
            let recommendationContainer = $('#recommendation-container');
            recommendationContainer.empty();

            data.forEach(product => {
                let productHtml = `
                    <div class="col-lg-3 col-md-4 col-sm-6">
                        <div class="product__item">
                            <div class="product__item__pic set-bg" style="background-image: url('data:image/jpeg;base64,${product.image}');">
                                <ul class="product__hover">
                                    <li><a href="#"><img src="/img/icon/heart.png" alt=""></a></li>
                                    <li><a href="#"><img src="/img/icon/compare.png" alt=""> <span>Compare</span></a></li>
                                    <li><a href="#"><img src="/img/icon/search.png" alt=""></a></li>
                                </ul>
                            </div>
                            <div class="product__item__text">
                                <h6><a href="#">${product.name}</a></h6>
                                <h5>$${product.price}</h5>
                            </div>
                        </div>
                    </div>
                `;
                recommendationContainer.append(productHtml);
            });
        },
        error: function(error) {
            console.error('Error fetching recommendations:', error);
        }
    });
	
	$.ajax({
	        url: `/recommendations/preference/view`,
	        method: 'GET',
	        success: function(data) {
	            let recommendationContainer = $('#userPreferences-container');
	            recommendationContainer.empty();

	            data.forEach(product => {
	                let productHtml = `
	                    <div class="col-lg-3 col-md-4 col-sm-6">
	                        <div class="product__item">
	                            <div class="product__item__pic set-bg" style="background-image: url('data:image/jpeg;base64,${product.image}');">
	                                <ul class="product__hover">
	                                    <li><a href="#"><img src="/img/icon/heart.png" alt=""></a></li>
	                                    <li><a href="#"><img src="/img/icon/compare.png" alt=""> <span>Compare</span></a></li>
	                                    <li><a href="#"><img src="/img/icon/search.png" alt=""></a></li>
	                                </ul>
	                            </div>
	                            <div class="product__item__text">
	                                <h6><a href="#">${product.name}</a></h6>
	                                <h5>$${product.price}</h5>
	                            </div>
	                        </div>
	                    </div>
	                `;
	                recommendationContainer.append(productHtml);
	            });
	        },
	        error: function(error) {
	            console.error('Error fetching recommendations:', error);
	        }
	    });
});
