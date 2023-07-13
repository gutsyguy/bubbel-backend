use super::*;

mod test_account_limbo_collect_garbage;
mod test_auth_collect_garbage;
mod test_create_user;
mod test_deauth_user;

pub fn new_data_state() -> DataState {
    let db_url = "postgresql://postgres:abc@localhost:5432/bubbel-test";

    std::process::Command::new("diesel")
        .arg("database")
        .arg("reset")
        .arg("--database-url")
        .arg(db_url)
        .output()
        .unwrap();

    DataState::new(db_url, "abcdefghijklmnop").unwrap()
}
