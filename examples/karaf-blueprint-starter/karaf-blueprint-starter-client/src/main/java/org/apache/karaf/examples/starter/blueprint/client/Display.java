package org.apache.karaf.examples.starter.blueprint.client;

import org.apache.karaf.examples.starter.blueprint.provider.Booking;
import org.apache.karaf.examples.starter.blueprint.provider.BookingService;

/**
 * Simple class getting the booking service (thanks to Blueprint), adding a random booking and displaying periodically.
 */
public class Display {

    private BookingService bookingService;

    private BookingDisplayThread thread;
    private boolean bookingThreadStarted = false;

    /**
     * This setter is used by Blueprint to inject the booking service.
     */
    public void setBookingService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Init method used to start the thread.
     */
    public void init() {
        BookingDisplayThread thread = new BookingDisplayThread(bookingService);
        thread.start();
    }

    /**
     * Destroy method used to stop the thread.
     */
    public void destroy() {
        thread.terminate();
    }

    /**
     * Very simple thread adding random booking and displaying the bookings on System.out every 5s.
     */
    private class BookingDisplayThread extends Thread {

        private BookingService bookingService;
        private volatile boolean running = true;

        public BookingDisplayThread(BookingService bookingService) {
            this.bookingService = bookingService;
        }

        @Override
        public void run() {
            while (running) {
                try {

                    // TODO test
                    Booking booking = new Booking("John Doo", "AF3030");
                    bookingService.add(booking);

                    System.out.println(displayBookings());
                    Thread.sleep(5000);
                } catch (Exception e) {
                    // nothing to do
                }
            }
        }

        private String displayBookings() {
            StringBuilder builder = new StringBuilder();
            for (Booking booking : bookingService.list()) {
                builder.append(booking.getId()).append(" | ").append(booking.getCustomer()).append(" | ").append(booking.getFlight()).append("\n");
            }
            return builder.toString();
        }

        public void terminate() {
            running = false;
        }

    }

}
