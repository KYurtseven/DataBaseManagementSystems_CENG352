CREATE TABLE "Customer"(
	customer_id SERIAL,
    email TEXT,
    password TEXT,
    first_name TEXT,
    last_name TEXT,
    session_count INTEGER,
    PRIMARY KEY (customer_id)
);

Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('first@ceng', '123123abc', 'first', 'user1', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('second@ceng', '123123abc', 'second', 'user2', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('third@ceng', '123123abc', 'third', 'user3', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('forth@ceng', '123123abc', 'forth', 'user4', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('fifth@ceng', '123123abc', 'fifth', 'user5', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('sixth@ceng', '123123abc', 'sixth', 'user6', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('seventh@ceng', '123123abc', 'seventh', 'user7', 0);
Insert INTO "Customer"( email, password, first_name, last_name, session_count) 
VALUES('eigth@ceng', '123123abc', 'eigth', 'user8', 0);

CREATE TABLE "Plan"(
	plan_id SERIAL,
    name TEXT,
    resolution TEXT,
    max_parallel_session INTEGER,
    monthly_fee REAL,
    PRIMARY KEY(plan_id));
    
Insert INTO "Plan"( name, resolution, max_parallel_session, monthly_fee)
VALUES('standard', 'sd', 1, 5.0);
Insert INTO "Plan"( name, resolution, max_parallel_session, monthly_fee)
VALUES('bronze', 'hd', 2, 10.0);
Insert INTO "Plan"( name, resolution, max_parallel_session, monthly_fee)
VALUES('silver', 'fullhd', 3, 15.0);
Insert INTO "Plan"( name, resolution, max_parallel_session, monthly_fee)
VALUES('gold', '4k', 4, 20.0);
Insert INTO "Plan"( name, resolution, max_parallel_session, monthly_fee)
VALUES('silver2', 'fullhd', 3, 10.0);

CREATE TABLE "Subscription"(
	s_id SERIAL,
    customer_id INTEGER unique,
    plan_id INTEGER,
    PRIMARY KEY(s_id),
    FOREIGN KEY(customer_id) REFERENCES "Customer"(customer_id),
    FOREIGN KEY(plan_id) REFERENCES "Plan"(plan_id));
    
    
Insert INTO "Subscription"(customer_id, plan_id)
VALUES(1, 1); -- customer 1 is standard
Insert INTO "Subscription"(customer_id, plan_id)
VALUES(2, 1); -- customer 2 is standard
Insert INTO "Subscription"( customer_id, plan_id)
VALUES(3, 2); -- customer 3 is bronze
Insert INTO "Subscription"( customer_id, plan_id)
VALUES(4, 2); -- customer 4 is bronze
Insert INTO "Subscription"( customer_id, plan_id)
VALUES(5, 3); -- customer 5 is silver
Insert INTO "Subscription"( customer_id, plan_id)
VALUES(6, 3); -- customer 6 is silver
Insert INTO "Subscription"( customer_id, plan_id)
VALUES(7, 4); -- customer 7 is gold 
Insert INTO "Subscription"( customer_id, plan_id)
VALUES(8, 4); -- customer 8 is gold

-- tt0111161 shawshank redemption

CREATE TABLE "Watched"(
	w_id SERIAL,
    movie_id TEXT,
    customer_id INTEGER,
	"when" DATE,
    Primary KEY(w_id),
    FOREIGN KEY(customer_id) REFERENCES "Customer"(customer_id));

Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0111161', 1, '2019-04-18'); -- customer 1 watched shawshank redemption
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0468569', 1, '2019-03-20'); -- customer 1 watched the dark knight
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0468569', 2, '2018-03-05'); -- customer 2 watched the dark knight
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0068646', 3, '2019-04-13'); -- customer 3 watched the godfather
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0108052', 4, '2015-10-13'); -- customer 4 watched schindler's list
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0110912', 5, '2016-04-18'); -- customer 5 watched pulp fiction
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0167260', 5, '2019-02-11'); -- customer 5 watched lotr return of the king
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0468569', 5, '2018-10-18'); -- customer 5 watched the dark knight
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0137523', 6, '2019-04-18'); -- customer 5 watched fight club
Insert INTO "Watched"( movie_id, customer_id, "when")
VALUES('tt0111161', 7, '2019-04-12'); -- customer 7 watched shawshank redemption
-- customer 8 did not watch anything


-- To delete all tables
-- drop table "Watched" CASCADE; drop table "Subscription" CASCADE; drop table "Customer" CASCADE ; drop table "Plan" CASCADE;



