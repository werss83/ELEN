$(document).ready(function () {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // One Time Care ///////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Recurrent Care //////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

    function setCalendarTitle() {
        document.getElementsByClassName("fc-left")[0].childNodes[0].innerHTML = "";
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
                left: "title",
                center: "",
                right: "add"
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
                setCalendarHeight()
                setCalendarTitle();
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
        setCalendarTitle();
        setCalendarHeight();
        setCalendarAxisHeaderWidth();
        setCalendarAddButtonEventListener();
        $(".fc-add-button").addClass("btn-cyan");
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
            "maxTime": new Date(0, 0, 0, 24, 0, 0)
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
            id: String(eventDay - 1).concat(String(eventStartHour * 60 + eventStartMin)).concat(String(eventEndHour * 60 + eventEndMin)),
            title: "",
            start: moment().day(eventDay).hour(eventStartHour + 1).minute(eventStartMin).second(0).zone(0),
            end: moment().day(eventDay).hour(eventEndHour + 1).minute(eventEndMin).second(0).zone(0),
            allDay: false
        }, true);
        $("#error-information").hide();
        return true;
        // } else {
        //     $("#error-information").show();
        //     return false;
        // }
    });

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Submit //////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    let $activeStepElement = getActiveStep();
    const $previousButton = $("#previous-button");
    const $nav = $("#nav-location");
    $activeStepElement.fadeIn();
    adaptNavigation($activeStepElement);

    function getActiveStep() {
        return $(".step.active");
    }

    $("[name='careType']").on("click", function () {
        const $careTypeStep = $(this).parents(".step");
        switch ($(this).val()) {
            case "ONE_TIME":
                $careTypeStep.data("next-step", "#step-one-time-care");
                break;
            case "RECURRENT":
                $careTypeStep.data("next-step", "#step-recurrent-care");
                break;
        }
        goNext();
    })

    function goNext() {
        const activeStepNumber = $activeStepElement.data("step");
        if ($activeStepElement.data("next-step") && checkStepValidity(activeStepNumber)) {
            $activeStepElement.removeClass("active");
            $activeStepElement.fadeOut(400, function () {
                const $newActiveStep = $($activeStepElement.data("next-step"));
                $newActiveStep.fadeIn();
                $newActiveStep.addClass("active");
                adaptNavigation($newActiveStep);
                initStep($newActiveStep);
                $activeStepElement = $newActiveStep;
            });
        }
    }

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
                initStep($newActiveStep);
                $activeStepElement = $newActiveStep;
            });
        }
    });

    function hideNavigation() {
        $nav.fadeOut();
    }

    function adaptNavigation($activeStep) {
        switch ($activeStep.data("step")) {
            case 1:
                $nav.fadeOut();
                break;
            case 2:
            case 3:
                $nav.fadeIn();
                break;
        }
    }

    function checkStepValidity(activeStep) {
        switch (activeStep) {
            case 1:
                return document.getElementById("choose-one-time-care").reportValidity();
            case 2:
                const dateValidity = document.getElementById("one-time-care-date").reportValidity();
                const startHourValidity = document.getElementById("one-time-care-start-hour").reportValidity();
                const durationValidity = document.getElementById("one-time-care-duration").reportValidity();
                return dateValidity && startHourValidity && durationValidity;
            case 3:
                if ($calendar.fullCalendar("clientEvents").length > 0) {
                    $calendarValidity.get(0).setCustomValidity("");
                } else {
                    $calendarValidity.get(0).setCustomValidity($calendarValidity.data("validity"));
                }
                return $calendarValidity.get(0).reportValidity();
        }
    }

    function initStep($newStep) {
        if (!$newStep[0].classList.contains("init-complete")) {
            switch ($newStep.data("step")) {
                case 1:
                    break;
                case 2:
                    initOneTimeCareStep();
                    $newStep.addClass("init-complete");
                    break;
                case 3:
                    initCalendar();
                    $newStep.addClass("init-complete");
                    break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Submit //////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    const $form = $("#schedule-new-care-form");

    document.getElementById("schedule-button")
        .addEventListener("click", function (event) {
            event.preventDefault();
            const activeStepNumber = $activeStepElement.data("step");
            checkStepValidity(activeStepNumber) ? $form.submit() : null;
        });
});
