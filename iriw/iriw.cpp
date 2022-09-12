#include <cstdio>
#include <atomic>
#include <condition_variable>
#include <functional>
#include <mutex>
#include <thread>

constexpr int ROUND = 1'0000'0000;
constexpr int NUM_THD = 4;
std::atomic_int a_x { 0 };
std::atomic_int a_y { 0 };
std::atomic_bool a_x_set_first { false };
std::atomic_bool a_y_set_first { false };

void reset_data() {
  a_x.store(0);
  a_y.store(0);
  a_x_set_first.store(false);
  a_y_set_first.store(false);
}

void wx() {
  a_x.store(1, std::memory_order_relaxed);
}

void wy() {
  a_y.store(1, std::memory_order_relaxed);
}

void seexy() {
  bool xis1 = a_x.load(std::memory_order_relaxed) == 1;
  asm volatile("" ::: "memory");
  bool yis1 = a_y.load(std::memory_order_relaxed) == 1;
  if (xis1 && !yis1) {
    a_x_set_first.store(true);
  }
}

void seeyx() {
  bool xis1 = a_x.load(std::memory_order_relaxed) == 1;
  asm volatile("" ::: "memory");
  bool yis1 = a_y.load(std::memory_order_relaxed) == 1;
  if (!xis1 && yis1) {
    a_y_set_first.store(true);
  }
}

void check_result() {
  if (a_x_set_first.load() && a_y_set_first.load()) {
    fprintf(stderr, "found\n");
  }
}

// Synchronization Point Implementation
std::atomic_int sync_cnt { 0 };
thread_local int thd_epoch = 0;
std::atomic_int cur_epoch { 0 };
std::mutex sync_mutex;
std::condition_variable sync_cond;

#ifdef SYNC_LOG
int log_seq(int idx) {
  return cur_epoch.load() * (NUM_THD + 1) + idx;
}
#endif // SYNC_LOG

void sync_primary(std::function<void()> on_sync) {
  while (sync_cnt.load() != NUM_THD) ;

#ifdef SYNC_LOG
  fprintf(stderr, "[%d]: sync succ in epoch %d\n",
      log_seq(NUM_THD), cur_epoch.load());
#endif // SYNC_LOG
  on_sync();
  reset_data();
  sync_cnt.store(0);
  cur_epoch.fetch_add(1);

  std::unique_lock<std::mutex> lk(sync_mutex);
  sync_cond.notify_all();
}

void sync_standby() {
  std::unique_lock<std::mutex> lk(sync_mutex);
  int idx = sync_cnt.fetch_add(1);
#ifdef SYNC_LOG
  fprintf(stderr, "[%d]: sync %d in epoch %d\n",
      log_seq(idx), idx, thd_epoch);
#endif // SYNC_LOG
  sync_cond.wait(lk, [] {
    return cur_epoch.load() == (thd_epoch + 1);
  });
  thd_epoch++;
}
// Synchronization Point Implementation

void round_wrapper(std::function<void()> func) {
  for (int i = 0; i < ROUND; i++) {
    func();
    sync_standby();
  }
}

int main() {
  std::thread t1(round_wrapper, wx);
  std::thread t2(round_wrapper, wy);
  std::thread t3(round_wrapper, seexy);
  std::thread t4(round_wrapper, seeyx);

  for (int i = 0; i < ROUND; i++) {
    sync_primary(check_result);
  }

  t1.join();
  t2.join();
  t3.join();
  t4.join();
}
