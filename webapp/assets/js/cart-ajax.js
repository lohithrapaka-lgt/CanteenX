/**
 * CanteenX Cart and Real-time Status AJAX Handler using jQuery (WT Syllabus Topic)
 */
$(document).ready(function () {

    // 1. AJAX Add-to-Cart with dynamic badge update
    $(".btn-add-cart-ajax").on("click", function (e) {
        e.preventDefault();
        const $btn = $(this);
        const itemId = $btn.data("item-id");
        const contextPath = $btn.data("context") || "";

        $btn.prop("disabled", true).text("Adding...");

        $.ajax({
            url: contextPath + "/cart",
            type: "POST",
            data: {
                action: "ADD",
                itemId: itemId,
                quantity: 1,
                ajax: "true"
            },
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    $("#cart-badge-count").text(data.totalCount).removeClass("d-none");
                    $btn.removeClass("btn-primary").addClass("btn-success").text("✓ Added");
                    setTimeout(function () {
                        $btn.removeClass("btn-success").addClass("btn-primary").text("Add to Cart").prop("disabled", false);
                    }, 1200);
                }
            },
            error: function () {
                $btn.removeClass("btn-primary").addClass("btn-danger").text("Error");
                setTimeout(function () {
                    $btn.removeClass("btn-danger").addClass("btn-primary").text("Add to Cart").prop("disabled", false);
                }, 1200);
            }
        });
    });

    // 2. Real-Time Order Status Polling for Student
    const $statusTracker = $("#order-status-tracker");
    if ($statusTracker.length > 0) {
        const orderId = $statusTracker.data("order-id");
        const contextPath = $statusTracker.data("context") || "";

        const pollInterval = setInterval(function () {
            $.ajax({
                url: contextPath + "/order-status",
                type: "GET",
                data: {
                    orderId: orderId,
                    ajax: "true"
                },
                dataType: "json",
                success: function (order) {
                    if (order && order.status) {
                        const currentBadge = $("#order-status-badge");
                        if (currentBadge.text().trim() !== order.status) {
                            location.reload(); // Refresh to update timeline stepper
                        }
                        if (order.status === "COMPLETED" || order.status === "CANCELLED") {
                            clearInterval(pollInterval);
                        }
                    }
                }
            });
        }, 5000); // Check every 5 seconds
    }
});
