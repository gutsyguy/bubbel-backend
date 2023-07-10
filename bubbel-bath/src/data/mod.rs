use super::*;
use std::string::ToString;

mod user;

pub use user::User;

pub struct DataState {
    pub db: PgConnection,
    pub user_salt: String,
}

impl DataState {
    pub fn new(db_url: &str, user_salt: &str) -> Result<Self, ConnectionError> {
        let db = PgConnection::establish(db_url)?;
        Ok(DataState {
            db,
            user_salt: user_salt.to_owned(),
        })
    }
}

#[derive(Serialize, Deserialize, Debug, Clone, PartialEq, Eq)]
#[serde(tag = "type")]
pub enum DatabaseError {
    NotFound,
    UniqueViolation,
    ForeignKeyViolation,
    NotNullViolation,
    CheckViolation,
    Internal { ierror: String },
}

impl ToString for DatabaseError {
    fn to_string(&self) -> String {
        match self {
            DatabaseError::NotFound => "NotFound".to_owned(),
            DatabaseError::Internal { ierror } => ierror.clone(),
            DatabaseError::UniqueViolation => "UniqueViolation".to_owned(),
            DatabaseError::ForeignKeyViolation => "ForeignKeyViolation".to_owned(),
            DatabaseError::NotNullViolation => "NotNullViolation".to_owned(),
            DatabaseError::CheckViolation => "CheckViolation".to_owned(),
        }
    }
}

impl From<diesel::result::Error> for DatabaseError {
    fn from(value: diesel::result::Error) -> Self {
        match value {
            diesel::result::Error::NotFound => Self::NotFound,
            diesel::result::Error::DatabaseError(kind, _) => match kind {
                diesel::result::DatabaseErrorKind::UniqueViolation => Self::UniqueViolation,
                diesel::result::DatabaseErrorKind::ForeignKeyViolation => Self::ForeignKeyViolation,
                diesel::result::DatabaseErrorKind::NotNullViolation => Self::NotNullViolation,
                diesel::result::DatabaseErrorKind::CheckViolation => Self::CheckViolation,
                _ => Self::Internal {
                    ierror: value.to_string(),
                },
            },
            _ => Self::Internal {
                ierror: value.to_string(),
            },
        }
    }
}
