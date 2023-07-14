use super::*;
use std::time::Duration;

pub async fn collect_garbage(app: Arc<AppState>) {
    tokio::join!(collect_auth_garbage(&app), collect_acc_limbo_garbage(&app));

    unreachable!("Garbage collection routines should run forever.");
}

//  TODO: Change this interval to be more realistic for production.
const AUTH_COLLECT_GARBAGE_INTERVAL: Duration = Duration::from_secs(10);
async fn collect_auth_garbage(app: &Arc<AppState>) {
    loop {
        {
            let mut auth = app.auth.write().unwrap();
            auth.collect_garbage();
        }
        tokio::time::sleep(AUTH_COLLECT_GARBAGE_INTERVAL).await;
    }
}

//  TODO: Change this interval to be more realistic for production.
const ACC_LIMBO_COLLECT_GARBAGE_INTERVAL: Duration = Duration::from_secs(10);
async fn collect_acc_limbo_garbage(app: &Arc<AppState>) {
    loop {
        {
            let mut db = app.db.lock().unwrap();
            let mut acc_limbo = app.acc_limbo.lock().unwrap();
            acc_limbo.collect_garbage(&mut db);
        }
        tokio::time::sleep(ACC_LIMBO_COLLECT_GARBAGE_INTERVAL).await;
    }
}
