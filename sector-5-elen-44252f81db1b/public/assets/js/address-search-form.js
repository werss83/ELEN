let autocomplete;

function fillInAddress() {
    // Get the place details from the autocomplete object.
    let place = autocomplete.getPlace();

    $("#address").val(place.address_components[0].long_name + " " + place.address_components[1].long_name)
    $("#zipCode").val(place.address_components[6].short_name)
    $("#postalCode").val(place.address_components[6].short_name)
    $("#city").val(place.address_components[2].long_name)

    $("#address").focus()
    $("#zipCode").focus()
    $("#postalCode").focus()
    $("#city").focus()
}

$(document).ready(function () {
    let options = {
        types: ['geocode'],
        componentRestrictions: {country: "fr"}
    };

    let input = document.getElementById('address');
    autocomplete = new google.maps.places.Autocomplete(input, options);
    autocomplete.setFields(['address_component']);
    autocomplete.addListener('place_changed', fillInAddress);
});
