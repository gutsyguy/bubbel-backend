use super::*;

#[derive(Queryable, Selectable, Insertable, Debug, Clone)]
#[diesel(table_name = crate::schema::users)]
#[diesel(check_for_backend(diesel::pg::Pg))]
pub struct User {
    pub username: String,
    pub password_hash: String,
    pub email: String,
    pub is_verified: bool,
}

impl User {
    pub fn get(db: &mut DataState, id: UserId) -> Result<Option<Self>, DatabaseError> {
        use crate::schema::users::dsl;

        dsl::users
            .select((
                dsl::username,
                dsl::password_hash,
                dsl::email,
                dsl::is_verified,
            ))
            .filter(dsl::id.eq(id.0))
            .load::<User>(&mut db.db)
            .map(|v| v.first().cloned())
            .map_err(DatabaseError::from)
    }

    pub fn remove(db: &mut DataState, id: UserId) -> Result<(), DatabaseError> {
        use crate::schema::users::dsl;

        diesel::delete(dsl::users)
            .filter(dsl::id.eq(id.0))
            .execute(&mut db.db)
            .map(|_| ())
            .map_err(DatabaseError::from)
    }
}
