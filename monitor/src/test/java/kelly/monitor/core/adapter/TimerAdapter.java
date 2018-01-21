//package kelly.monitor.core.adapter;
//
//import kelly.monitor.core.Timer;
//
//import java.util.concurrent.TimeUnit;
//
//public class TimerAdapter implements Timer {
//
//    private final com.codahale.metrics.Timer _timer;
//
//    public TimerAdapter(com.codahale.metrics.Timer timer) {
//        this._timer = timer;
//    }
//
//    @Override
//    public void update(long duration, TimeUnit unit) {
//        _timer.update(duration, unit);
//    }
//
//    @Override
//    public Context time() {
//        ContextAdapter adapter = new ContextAdapter();
//        adapter._context = _timer.time();
//        return adapter;
//    }
//
//    private class ContextAdapter implements Context {
//        private com.codahale.metrics.Timer.Context _context;
//
//        @Override
//        public long stop() {
//            return _context.stop();
//        }
//    }
//}