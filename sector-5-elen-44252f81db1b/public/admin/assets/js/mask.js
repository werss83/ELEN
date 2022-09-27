(function ($) {
    'use strict';

    $.mask.definitions['~'] = '[+-]';
    $("input.date-input").mask('99/99/9999');
    $("input.datetime-input").mask('99/99/9999 99:99');
    // $('#date').mask('99/99/9999');
    // $('#phone').mask('(999) 999-9999');
    // $('#phoneExt').mask('(999) 999-9999? x99999');
    // $('#iphone').mask('+33 999 999 999');
    // $('#tin').mask('99-9999999');
    // $('#ccn').mask('9999 9999 9999 9999');
    // $('#ssn').mask('999-99-9999');
    // $('#currency').mask('999,999,999.99');
    // $('#product').mask('a*-999-a999', {
    //     placeholder: ' '
    // });
    // $('#eyescript').mask('~9.99 ~9.99 999');
    // $('#po').mask('PO: aaa-999-***');
    // $('#pct').mask('99%');
    // $('#phoneAutoclearFalse').mask('(999) 999-9999', {
    //     autoclear: false10 / 03 / 1991
    // });
    // $('#phoneExtAutoclearFalse').mask('(999) 999-9999? x99999', {
    //     autoclear: false
    // });
    // $('input').blur(function () {
    //     $('#info').html('Unmasked value: ' + $(this).mask());
    // }).dblclick(function () {
    //     $(this).unmask();
    // });
})(jQuery);
