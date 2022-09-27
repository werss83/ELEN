$(document).ready(function () {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Location ////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Google map and marker
    // let map = new google.maps.Map(document.getElementById("map"), {
    //     center: {lat: 47.0780911, lng: 2.3632841},
    //     zoom: 6,
    //     disableDefaultUI: true
    // });
    //
    // let marker = new google.maps.Marker({
    //     map: map,
    //     anchorPoint: new google.maps.Point(0, -29)
    // });

    // Google address search
    const searchAddress = document.getElementById('search-address');
    const address = document.getElementById('address');
    const zipCode = document.getElementById('zip-code');
    const city = document.getElementById('city');

    const options = {
        types: ['geocode'],
        componentRestrictions: {country: "fr"}
    };

    let autocomplete = new google.maps.places.Autocomplete(searchAddress, options);
    // autocomplete.bindTo("bounds", map)
    autocomplete.setFields(['address_components', 'geometry', 'icon', 'name']);
    autocomplete.addListener('place_changed', () => {
        const place = autocomplete.getPlace();

        address.value = place.address_components[0] && place.address_components[1] ? place.address_components[0].long_name + " " + place.address_components[1].long_name : "";
        zipCode.value = place.address_components[6] ? place.address_components[6].short_name : "";
        city.value = place.address_components[2] ? place.address_components[2].long_name : "";

        // if (place.geometry) {
        //     marker.setVisible(false);
        //
        //     map.setCenter(place.geometry.location);
        //     map.setZoom(15);
        //
        //     marker.setPosition(place.geometry.location);
        //     marker.setVisible(true);
        // }
    });

    $(searchAddress).on("change", () => {
        searchAddress.setCustomValidity("");
        address.value = "";
        zipCode.value = "";
        city.value = "";
    });

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Care Type ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    function initCareTypeStep() {
        $("#speaker-careType-sitter").html($("#speaker-careType-sitter").data("messages").replace("{0}", moment().millisecond() % 16 + 4));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // One Time Care ///////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    const $oneTimeCareDate = $("#one-time-care-date");
    const $oneTimeCareStartHour = $("#one-time-care-start-hour");
    const $oneTimeCareDuration = $("#one-time-care-duration");

    let minDate = 1;

    function initOneTimeCareStep() {
        // Datepicker
        $oneTimeCareDate.datepicker({
            "dateFormat": "dd/mm/yy",
            "minDate": minDate,
        });
        $oneTimeCareDate.datepicker($.datepicker.regional["fr"]);
        $oneTimeCareDate.on("change", function () {
            const date = $(this).datepicker("getDate");

            // if (isToday(date)) {
            //     const now = new Date();
            //     let minHour = now.getHours() + 2;
            //     const round = [0, 15, 30, 45, 60];
            //     let minMin = now.getMinutes();
            //     minMin = round.reduce(function (previous, current) {
            //         return (Math.abs(current - minMin) < Math.abs(previous - minMin) ? current : previous);
            //     });
            //     refreshTimePicker($oneTimeCareStartHour, new Date(0, 0, 0, minHour, minMin, 0), new Date(0, 0, 0, 21, 0, 0));
            if (isTomorrow(date)) {
                const now = new Date();
                const minHour = now.getHours();
                const round = [0, 15, 30, 45, 60];
                let minMin = now.getMinutes();
                minMin = round.reduce(function (previous, current) {
                    return (Math.abs(current - minMin) < Math.abs(previous - minMin) ? current : previous);
                })
                refreshTimePicker($oneTimeCareStartHour, new Date(0, 0, 0, minHour, minMin, 0));
            } else {
                refreshTimePicker($oneTimeCareStartHour, new Date(0, 0, 0, 0, 0, 0));
            }
        })

        function isToday(date) {
            const today = new Date();
            return date.getDate() === today.getDate() &&
                date.getMonth() === today.getMonth() &&
                date.getFullYear() === today.getFullYear();
        }

        function isTomorrow(date) {
            const today = new Date();
            const tomorrow = new Date(today.setDate(today.getDate() + 1));
            return date.getDate() === tomorrow.getDate() &&
                date.getMonth() === tomorrow.getMonth() &&
                date.getFullYear() === tomorrow.getFullYear();
        }

        // Timepicker
        function generateTimePicker($timePickerInput, minTime, maxTime) {
            minTime = minTime || new Date(0, 0, 0, 0, 0, 0);
            maxTime = maxTime || new Date(0, 0, 0, 23, 45, 0);
            $timePickerInput.timepicker({
                "timeFormat": "H:i",
                "step": 15,
                "useSelect": true,
                "className": "w-100",
                "minTime": minTime,
                "maxTime": maxTime
            })
        }

        function refreshTimePicker($timePickerInput, minTime, maxTime) {
            const $select = $("select[name='ui-timepicker-oneTimeCare.startHour']");
            $select.select2("destroy");
            $select.remove();
            generateTimePicker($timePickerInput, minTime, maxTime);
            const coefficient = 1000 * 60 * 15;
            const nowRounded = new Date(Math.round(new Date() / coefficient) * coefficient);
            const setTime = new Date(0, 0, 0) < minTime ? minTime : nowRounded;
            $oneTimeCareStartHour.timepicker("setTime", setTime);
            $("select[name='ui-timepicker-oneTimeCare.startHour']").select2({
                minimumResultsForSearch: -1
            });
        }

        generateTimePicker($oneTimeCareStartHour);
        const coefficient = 1000 * 60 * 15;
        const nowRounded = new Date(Math.round(new Date() / coefficient) * coefficient);
        $oneTimeCareStartHour.timepicker("setTime", nowRounded);
        $("select[name='ui-timepicker-oneTimeCare.startHour']").select2({
            minimumResultsForSearch: -1
        });

        // Duration slider
        new Powerange($oneTimeCareDuration.get(0), {
            callback: function () {
                $(".range-handle").html($oneTimeCareDuration.val());
            },
            hideRange: true,
            min: 1,
            max: 10,
            step: 1
        })
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Recurrent Care //////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Calendar
    const $calendar = $("#calendar");
    const $calendarValidity = $("#calendar-validity");

    function getHeaderDisplaySize() {
        if (window.innerWidth < 600) {
            return "ddd";
        } else {
            return "dddd";
        }
    }

    function setCalendarHeight() {
        const header = document.getElementsByClassName("fc-toolbar")[0];
        const th = document.getElementsByClassName("fc-axis")[0];
        const tr = document.getElementsByClassName("fc-axis")[2];
        // height = header + header margin + th + 24tr + 3px (to avoid scrolling bar)
        const height = header.offsetHeight + parseFloat(window.getComputedStyle(header)["margin-bottom"]) + th.offsetHeight + 18 * tr.offsetHeight + 3;
        $calendar.fullCalendar("option", "height", height);
        $(".fc-scroller").get(0).scroll(0, 650)
    }

    function setCalendarAxisHeaderWidth() {
        $(".fc-axis.fc-widget-header").get(0).style.width = $(".fc-axis.fc-widget-content").get(0).offsetWidth + "px";
    }

    function setCalendarAddButtonEventListener() {
        $(".fc-add-button")
            .attr("data-toggle", "modal")
            .attr("data-target", "#add-event-modal");
    }

    function createEventInputs(event) {
        if (event.id && !$("#" + event.id)[0]) {
            const date = event.start.format("DD/MM/YYYY");
            const startHour = event.start.format("HH:mm");

            const startMinutes = event.start.hours() * 60 + event.start.minutes();
            const endMinutes = event.end.hours() * 60 + event.end.minutes();

            const duration = ((endMinutes - startMinutes + 1440) % 1440) / 60; // Avoid negative duration when event end at midnight

            const $eventContainer = $("#events");
            const eventNumber = $eventContainer.find("div").length;

            $eventContainer.append(
                '<div id="' + event.id + '">\n' +
                '    <input type="hidden" name="recurrentCare[' + eventNumber + '].date" value="' + date + '">\n' +
                '    <input type="hidden" name="recurrentCare[' + eventNumber + '].startHour" value="' + startHour + '">\n' +
                '    <input type="hidden" name="recurrentCare[' + eventNumber + '].duration" value="' + duration + '">\n' +
                '</div>'
            );
        }
    }

    function deleteEventInputs(event) {
        $("#" + event.id).remove();
    }

    function updateEvent(event) {
        const newId = getEventId(event.start, event.end);
        event._id = newId;
        event.id = newId;
        $calendar.fullCalendar("updateEvent", event);
    }

    function getEventId(start, end) {
        return "" + ((start.days() + 6) % 7) + (start.hours() * 60 + start.minutes()) + (end.hours() * 60 + end.minutes())
    }

    function initCalendar() {
        $calendar.fullCalendar({
            defaultView: "agendaWeek",
            allDaySlot: false,
            views: {
                week: {
                    columnFormat: getHeaderDisplaySize(),
                    slotLabelFormat: "HH:mm"
                }
            },
            locale: "fr",
            header: {
                left: "add",
                center: "",
                right: "next"
            },
            customButtons: {
                add: {
                    text: $calendar.data("button-add"),
                    click: function () {
                        const $eventStartHour = $("#event-start-hour");
                        const $eventEndHour = $("#event-end-hour");

                        if (!$eventStartHour.hasClass("ui-timepicker-input")) {
                            $eventStartHour.timepicker({
                                "timeFormat": "H:i",
                                "step": 15,
                                "useSelect": true,
                                "className": "event-hour",
                                "maxTime": "23:45"
                            });
                        }
                        if (!$eventEndHour.hasClass("ui-timepicker-input")) {
                            $eventEndHour.timepicker({
                                "timeFormat": "H:i",
                                "step": 15,
                                "useSelect": true,
                                "className": "event-hour",
                                "minTime": "01:00",
                                "maxTime": "23:45"
                            });
                        }

                        const $eventDaySelect = $("#event-day");
                        if (!$eventDaySelect.data("select2-id")) {
                            $eventDaySelect.select2({
                                minimumResultsForSearch: -1
                            });
                        }
                        const $eventStartHourSelect = $("select[name='ui-timepicker-event-start-hour']");
                        if (!$eventStartHourSelect.data("select2-id")) {
                            $eventStartHourSelect.select2();
                        }
                        const $eventEndHourSelect = $("select[name='ui-timepicker-event-end-hour']");
                        if (!$eventEndHourSelect.data("select2-id")) {
                            $eventEndHourSelect.select2();
                        }
                    }
                },
                next: {
                    text: $calendar.data("button-next"),
                    click: function () {
                        $nextButton.click()
                    }
                }
            },
            slotDuration: "00:15:00",
            slotLabelInterval: "01:00:00",
            selectable: true,
            selectHelper: true,
            selectOverlap: true,
            eventOverlap: false,
            editable: true,
            eventConstraint: {
                start: "00:00",
                end: "24:00"
            },
            selectConstraint: {
                start: "00:00",
                end: "24:00"
            },
            windowResize: function (view) {
                $calendar.fullCalendar("option", "columnFormat", getHeaderDisplaySize());
                setCalendarHeight();
                setCalendarAxisHeaderWidth();
                setCalendarAddButtonEventListener();
            },
            select: function (start, end, jsEvent, view) {
                if ((end.hours() * 60 + end.minutes()) - (start.hours() * 60 + start.minutes()) < 60) {
                    end = moment(start).add(1, "h");
                }

                const eventId = getEventId(start, end);
                $calendar.fullCalendar("renderEvent", {
                    id: eventId,
                    title: "",
                    start: start,
                    end: end,
                    allDay: false
                }, true);
            },
            eventDrop: function (event, delta, revertFunc, jsEvent, ui, view) {
                if ((event.end.hours() * 60 + event.end.minutes()) - (event.start.hours() * 60 + event.start.minutes()) < 60) {
                    event.end = moment(event.start).add(1, "h");
                }

                deleteEventInputs(event);
                updateEvent(event);
            },
            eventResize: function (event, delta, revertFunc, jsEvent, ui, view) {
                if ((event.end.hours() * 60 + event.end.minutes()) - (event.start.hours() * 60 + event.start.minutes()) < 60) {
                    event.end = moment(event.start).add(1, "h");
                }

                deleteEventInputs(event);
                updateEvent(event);
            },
            eventRender: function (event, element) {
                element.append("<i class='event-close-button fa fa-times-circle'></i>");
                element.find(".event-close-button").on("click", function () {
                    $calendar.fullCalendar("removeEvents", event.id);
                });
                createEventInputs(event);
            },
            eventDestroy: function (event, element, view) {
                deleteEventInputs(event);
            },
            eventAfterAllRender: function (view) {
                $(".fc-helper-container").empty();
            }
        });
        setCalendarHeight();
        setCalendarAxisHeaderWidth();
        setCalendarAddButtonEventListener();
        $(".fc-add-button").addClass("btn-pink");
        $(".fc-next-button").addClass("btn-cyan");
    }

    $("#add-event-modal").on("hide.bs.modal", function () {
        $("#error-information").hide();
        return true;
    });

    $("#event-start-hour").on("change", function () {
        const $select = $("select[name='ui-timepicker-event-end-hour']");
        $select.select2("destroy");
        $select.remove();
        $("#event-end-hour").timepicker({
            "timeFormat": "H:i",
            "step": 15,
            "useSelect": true,
            "className": "event-hour",
            "minTime": new Date(0, 0, 0, (parseInt($(this).val().substring(0, 2)) + 25) % 24, parseInt($(this).val().substring(3, 5)), 0),
            "maxTime": new Date(0, 0, 0, 23, 45, 0)
        });
        $("#event-end-hour").timepicker("setTime", new Date(0, 0, 0, (parseInt($(this).val().substring(0, 2)) + 25) % 24, parseInt($(this).val().substring(3, 5)), 0));
        $("select[name='ui-timepicker-event-end-hour']").select2();
    });

    $("#add-event-button").on("click", function (event) {
        const eventDay = parseInt($("#event-day").val());
        const eventStartHour = parseInt($("#event-start-hour").val().substring(0, 2));
        const eventEndHour = parseInt($("#event-end-hour").val().substring(0, 2));
        const eventStartMin = parseInt($("#event-start-hour").val().substring(3, 5));
        const eventEndMin = parseInt($("#event-end-hour").val().substring(3, 5));

        const startHour = moment().day(eventDay).hour(eventStartHour).minute(0).second(1);
        const endHour = moment().day(eventDay).hour(eventEndHour - 1).minute(59).second(59);

        function checkEventAvailability() {
            let availability = true;
            $calendar.fullCalendar("clientEvents").forEach(function (event) {
                if (startHour.isAfter(event.start) && startHour.isBefore(event.end)) {
                    availability = false;
                } else if (endHour.isAfter(event.start) && endHour.isBefore(event.end)) {
                    availability = false;
                }
            });
            if (eventStartHour === eventEndHour) {
                availability = false;
            }
            return availability;
        }

        // if (checkEventAvailability()) {
        $calendar.fullCalendar("renderEvent", {
            id: String(eventDay - 1).concat(String(eventStartHour)).concat(String(eventEndHour)),
            title: "",
            start: moment().day(eventDay).hour(eventStartHour).minute(eventStartMin).second(1),
            end: moment().day(eventDay).hour(eventEndHour).minute(eventEndMin).second(0),
            allDay: false
        });
        $("#error-information").hide();
        return true;
        // } else {
        //     $("#error-information").show();
        //     return false;
        // }
    });

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Child selection /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    function appendNewChild() {
        const newChildIndex = $("#children .children-age").length;
        const lessThanThreeMessages = $(".children-label-lessThanThree").html();
        const lessThanSixMessages = $(".children-label-lessThanSix").html();
        const moreThanSixMessages = $(".children-label-moreThanSix").html();
        const childcareLabel = $("label.usual-childcare").html();
        const childcareOptions = $("[name='children[0].childcare']").html();
        const childcareValidity = $(".usual-childcare").data("validity");

        const element = '' +
            '<div class="children-age-wrapper">\n' +
            '    <div class="children-age">\n' +
            '        <div class="radio-input-wrapper">\n' +
            '            <input required type="radio" id="less-than-three[' + newChildIndex + ']" name="children[' + newChildIndex + '].category" value="UNDER_3_YEARS">\n' +
            '            <label for="less-than-three[' + newChildIndex + ']">\n' +
            '                <div class="children-label-icon">🍼</div>\n' +
            '                <div>' + lessThanThreeMessages + '</div>\n' +
            '                <select id="children[' + newChildIndex + '].childcare" name="children[' + newChildIndex + '].childcare" class="form-control usual-childcare" data-validity="' + childcareValidity + '">\n' +
            '                ' + childcareOptions + '\n' +
            '                </select>\n' +
            '            </label>\n' +
            '        </div>\n' +
            '        <div class="radio-input-wrapper">\n' +
            '            <input required type="radio" id="less-than-six[' + newChildIndex + ']" name="children[' + newChildIndex + '].category" value="UNDER_6_YEARS">\n' +
            '            <label for="less-than-six[' + newChildIndex + ']">\n' +
            '                <div class="children-label-icon">🤸</div>\n' +
            '                <div>' + lessThanSixMessages + '</div>\n' +
            '            </label>\n' +
            '        </div>\n' +
            '        <div class="radio-input-wrapper">\n' +
            '            <input required type="radio" id="more-than-six[' + newChildIndex + ']" name="children[' + newChildIndex + '].category" value="OVER_6_YEARS">\n' +
            '            <label for="more-than-six[' + newChildIndex + ']">\n' +
            '                <div class="children-label-icon">📖</div>\n' +
            '                <div>' + moreThanSixMessages + '</div>\n' +
            '            </label>\n' +
            '        </div>\n' +
            '    </div>\n' +
            '</div>';

        $("#children > .child-action").before(element);
    }

    $(".add-child").on("click", function () {
        appendNewChild();
    });

    $(".remove-child").on("click", function () {
        const $childrenAgeWrapper = $(".children-age-wrapper");
        if ($childrenAgeWrapper.length > 1) {
            $childrenAgeWrapper.last().remove();
        }
    })

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Parent Criteria /////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    const $parentCriteria = $("#step-parent-criteria");

    function initParentCriteria(singleParent, childrenNumber) {
        $.ajax({
            method: "POST",
            data: {
                singleParent: singleParent,
                childrenNumber: childrenNumber
            },
            url: $parentCriteria.data("income-url"),
            error: function (jqXHR, status, error) {
            },
            success: function (data, status, jqXHR) {
                const $incomeBelowMinLabel = $("#income-below-min-label");
                $incomeBelowMinLabel.html($incomeBelowMinLabel.data("value").replace("{0}", Math.round(data.lowLimit / 100).toLocaleString())
                    .replace(" ", " "));
                document.getElementById("income-below-min").checked = false;
                const $incomeBelowMaxLabel = $("#income-below-max-label");
                $incomeBelowMaxLabel.html($incomeBelowMaxLabel.data("value").replace("{0}", Math.round(data.lowLimit / 100).toLocaleString()).replace("{1}", Math.round(data.highLimit / 100).toLocaleString())
                    .replace(" ", " "));
                document.getElementById("income-below-max").checked = false;
                const $incomeAboveMinLabel = $("#income-above-max-label");
                $incomeAboveMinLabel.html($incomeAboveMinLabel.data("value").replace("{0}", Math.round(data.highLimit / 100).toLocaleString())
                    .replace(" ", " "));
                document.getElementById("income-above-max").checked = false;
            }
        });
    }

    $("#single-parent").on("change", function () {
        initParentCriteria($(this).get(0).checked, $("#children .children-age").length);
    });

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Registration ////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    $("#email").on("change", function () {
        this.setCustomValidity("");
    });

    function initRegistration() {
        const $speakerText = $("#sign-up-speaker-text")
        $.ajax({
            method: "POST",
            async: false,
            data: $form.serialize(),
            url: "/onboarding/parent/subsidizeAmount",
            error: function (jqXHR, status, error) {
                $speakerText.html($speakerText.data("message"));
            },
            success: function (data, status, jqXHR) {
                if (data.subsidizeAmount > 0) {
                    $speakerText.html($speakerText.data("message-subsidize").replace("{0}", data.subsidizeAmount / 100));
                } else {
                    $speakerText.html($speakerText.data("message"));
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Stepper /////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    let $activeStepElement = getActiveStep();
    const $previousButton = $("#previous-button");
    const $nextButton = $("#next-button");
    const $signUpWrapper = $("#sign-up-wrapper");
    $activeStepElement.fadeIn();
    adaptNavigation($activeStepElement);

    $nextButton.on("click", function () {
        const activeStepNumber = $activeStepElement.data("step");
        if ($activeStepElement.data("next-step") && checkStepValidity(activeStepNumber)) {
            $activeStepElement.removeClass("active");
            hideNavigation();
            $activeStepElement.fadeOut(400, function () {
                const $newActiveStep = $($activeStepElement.data("next-step"));
                $newActiveStep.fadeIn();
                $newActiveStep.addClass("active");
                adaptNavigation($newActiveStep);
                initStep($newActiveStep);
                updateProgressBar($activeStepElement, $newActiveStep);
                $activeStepElement = $newActiveStep;
            });
        }
    });

    $previousButton.on("click", function (event) {
        event.preventDefault();
        if ($activeStepElement.data("previous-step")) {
            $activeStepElement.removeClass("active");
            hideNavigation();
            $activeStepElement.fadeOut(400, function () {
                const $newActiveStep = $($activeStepElement.data("previous-step"));
                $newActiveStep.fadeIn();
                $newActiveStep.addClass("active");
                adaptNavigation($newActiveStep);
                updateProgressBar($activeStepElement, $newActiveStep);
                $activeStepElement = $newActiveStep;
            });
        } else {
            window.location = $(this).data("home");
        }
    });

    $("[name='careType']").on("click", function () {
        const $careTypeStep = $(this).parents(".onboarding-step");
        const $childrenStep = $("#step-children");
        switch ($(this).val()) {
            case "ONE_TIME":
                $careTypeStep.data("next-step", "#step-one-time-care");
                $childrenStep.data("previous-step", "#step-one-time-care");
                break;
            case "RECURRENT":
                $careTypeStep.data("next-step", "#step-recurrent-care");
                $childrenStep.data("previous-step", "#step-recurrent-care");
                break;
        }
        $nextButton.click();
    });

    $("#find-sitter-button").on("click", function () {
        $nextButton.click();
        return false;
    })

    function getActiveStep() {
        return $(".onboarding-step.active");
    }

    function hideNavigation() {
        $nextButton.fadeOut();
        $nextButton.attr("disabled", "disabled");
        $previousButton.attr("disabled", "disabled");
        $signUpWrapper.fadeOut();
    }

    function adaptNavigation($activeStep) {
        $previousButton.removeAttr("disabled", "disabled");

        function navigation(next, signUp) {
            if (next) {
                $nextButton.fadeIn();
                $nextButton.removeAttr("disabled", "disabled");
            } else {
                $nextButton.fadeOut();
                $nextButton.attr("disabled", "disabled");
            }

            signUp ? $signUpWrapper.fadeIn() : $signUpWrapper.fadeOut();
        }

        switch ($activeStep.data("step")) {
            case 1:
            case 2:
                navigation(false, false);
                break;
            case 3:
                navigation(true, false);
                break;
            case 4:
                navigation(false, false);
                break;
            case 5:
            case 6:
                navigation(true, false);
                break;
            case 7:
                navigation(false, true);
                break;
        }
    }

    function checkStepValidity(activeStep) {
        switch (activeStep) {
            case 1:
                const addressValidity = document.getElementById("address").checkValidity();
                const zipCodeValidity = document.getElementById("zip-code").checkValidity();
                const cityValidity = document.getElementById("city").checkValidity();

                const searchAddress = document.getElementById("search-address");

                if (addressValidity && zipCodeValidity && cityValidity) {
                    searchAddress.setCustomValidity("");
                } else {
                    searchAddress.setCustomValidity($(searchAddress).data("invalid"));
                }
                return searchAddress.reportValidity();
            case 2:
                return document.getElementById("choose-one-time-care").reportValidity();
            case 3:
                const dateValidity = document.getElementById("one-time-care-date").reportValidity();
                const startHourValidity = document.getElementById("one-time-care-start-hour").reportValidity();
                const durationValidity = document.getElementById("one-time-care-duration").reportValidity();
                return dateValidity && startHourValidity && durationValidity;
            case 4:
                if ($calendar.fullCalendar("clientEvents").length > 0) {
                    $calendarValidity.get(0).setCustomValidity("");
                } else {
                    $calendarValidity.get(0).setCustomValidity($calendarValidity.data("validity"));
                }
                return $calendarValidity.get(0).reportValidity();
            case 5:
                let childValidity = true;
                let usualChildCareValidity = true;
                $(".children-age").each(function (index) {
                    childValidity = childValidity && document.getElementById("more-than-six[" + index + "]").reportValidity();

                    if (document.getElementById("less-than-three[" + index + "]").checked) {
                        const childCare = document.getElementById("children[" + index + "].childcare");
                        if (childCare.options[childCare.selectedIndex].value === "null") {
                            childCare.setCustomValidity($(childCare).data("validity"));
                        } else {
                            childCare.setCustomValidity("");
                        }
                        usualChildCareValidity = childCare.reportValidity();
                    }
                });
                return childValidity && usualChildCareValidity;
            case 6:
                const incomeValidity = document.getElementById("income-below-max").reportValidity();
                const cafValidity = document.getElementById("caf-number").reportValidity()
                return incomeValidity && cafValidity;
            case 7:
                const password = document.getElementById("password");
                const passwordConfirmation = document.getElementById("password-confirmation");
                const email = document.getElementById("email");

                if (password) {
                    if (password.value !== passwordConfirmation.value) {
                        password.setCustomValidity($(password).data("do-not-match"));
                    } else {
                        password.setCustomValidity("");
                    }
                }

                $.ajax({
                    method: "POST",
                    async: false,
                    data: {
                        email: email.value,
                    },
                    url: "/onboarding/parent/emailAvailable",
                    error: function (jqXHR, status, error) {
                        return false;
                    },
                    success: function (data, status, jqXHR) {
                        if (data.available) {
                            email.setCustomValidity("");
                        } else {
                            email.setCustomValidity(data.error ? data.error + "." : "");
                        }
                    }
                });

                const phoneNumberValidity = document.getElementById("phone-number").reportValidity();
                const emailValidity = email.reportValidity();
                const lastNameValidity = document.getElementById("last-name").reportValidity();
                const firstNameValidity = document.getElementById("first-name").reportValidity();

                return firstNameValidity
                    && lastNameValidity
                    && emailValidity
                    && (!password || password.reportValidity())
                    && phoneNumberValidity;
        }
    }

    function initStep($newStep) {
        if (!$newStep[0].classList.contains("init-complete")) {
            switch ($newStep.data("step")) {
                case 1:
                case 2:
                    initCareTypeStep();
                    break;
                case 3:
                    initOneTimeCareStep();
                    $newStep.addClass("init-complete");
                    break;
                case 4:
                    initCalendar();
                    $newStep.addClass("init-complete");
                    break;
                case 5:
                    break;
                case 6:
                    initParentCriteria($parentCriteria.get(0).checked, $("#children .children-age").length)
                    break;
                case 7:
                    initRegistration();
                    break;
            }
        }
    }

    function updateProgressBar($oldStep, $newStep) {
        $("#step-progress-bar").removeClass("w-" + $oldStep.data("progress"))
            .addClass("w-" + $newStep.data("progress"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Submit //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    const $form = $("#onboarding-form");

    document.getElementById("sign-up-button")
        .addEventListener("click", (event) => {
            event.preventDefault();
            $oneTimeCareDate.removeAttr("required");
            $oneTimeCareStartHour.removeAttr("required");
            $oneTimeCareDuration.removeAttr("required");
            if (checkStepValidity(7)) {
                $form.submit();
            } else {
                return;
            }
        });

    function adaptFormToSocialRegistration() {
        $("#first-name").removeAttr("required");
        $("#last-name").removeAttr("required");
        $("#email").removeAttr("required");
        $("#password").removeAttr("required");
        $("#password-confirmation").removeAttr("required");
        $("#phone-number").removeAttr("required");
    }

    const facebookSignUpButton = document.getElementById("facebook-sign-up-button");
    if (facebookSignUpButton) {
        facebookSignUpButton.addEventListener("click", (event) => {
            adaptFormToSocialRegistration();
            $form.get(0).action = $form.data("facebook-action");
            $form.submit();
        });
    }

    const googleSignUpButton = document.getElementById("google-sign-up-button");
    if (googleSignUpButton) {
        googleSignUpButton.addEventListener("click", (event) => {
            adaptFormToSocialRegistration();
            $form.get(0).action = $form.data("google-action");
            $form.submit();
        });
    }
});
