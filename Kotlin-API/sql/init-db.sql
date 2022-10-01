CREATE TABLE "Vote" (
	"Id" serial NOT NULL,
	"Title" varchar(255) NOT NULL,
	"DateStart" DATE NOT NULL,
	"DateFinish" DATE NOT NULL,
	"Status" varchar(50) NOT NULL,
	CONSTRAINT "Vote_pk" PRIMARY KEY ("Id")
) WITH (
  OIDS=FALSE
);

CREATE TABLE "Question" (
	"Id" serial NOT NULL,
	"Vote" integer NOT NULL,
	"Content" TEXT NOT NULL,
	"DateVote" DATE NOT NULL,
	CONSTRAINT "Question_pk" PRIMARY KEY ("Id")
) WITH (
  OIDS=FALSE
);

CREATE TABLE "User" (
	"Id" serial NOT NULL,
	"LastName" varchar(50) NOT NULL,
	"FirstName" varchar(50) NOT NULL,
	"Email" varchar(50) NOT NULL,
	"Phone" varchar(15) NOT NULL,
	"Status" bool NOT NULL,
	CONSTRAINT "User_pk" PRIMARY KEY ("Id")
) WITH (
  OIDS=FALSE
);



CREATE TABLE "Choice" (
	"Id" serial NOT NULL,
	"Question" integer NOT NULL,
	"User" integer NOT NULL,
	"ChoiceUser" varchar(255) NOT NULL,
	CONSTRAINT "Choice_pk" PRIMARY KEY ("Id")
) WITH (
  OIDS=FALSE
);

ALTER TABLE "Question" ADD CONSTRAINT "Question_fk0" FOREIGN KEY ("Vote") REFERENCES "Vote"("Id");
ALTER TABLE "Choice" ADD CONSTRAINT "Choice_fk0" FOREIGN KEY ("Question") REFERENCES "Question"("Id");
ALTER TABLE "Choice" ADD CONSTRAINT "Choice_fk1" FOREIGN KEY ("User") REFERENCES "User"("Id");

INSERT INTO "Vote" ("Title", "DateStart", "DateFinish", "Status")
VALUES (
    'Предпочтения',
    '2022-09-22',
    '2022-10-10',
    'Действующий'
);

INSERT INTO "User" ("LastName", "FirstName", "Email", "Phone", "Status")
VALUES (
    'Петров',
    'Василий',
    'test@tsa.sda',
    '+7984893234234',
    false
);

INSERT INTO "Question" ("Vote", "Content", "DateVote")
VALUES (
    1,
    'Какой любимый цвет? ',
    '2022-09-25'
  );

INSERT INTO "Question" ("Vote", "Content", "DateVote")
VALUES (
    1,
    ' Какое блюдо нравится больше? ',
    '2022-09-25'
  );

INSERT INTO "Question" ("Vote", "Content", "DateVote")
VALUES (
    1,
    ' Предпочитаете книги или просмотр фильмов?',
    '2022-09-25'
  );

INSERT INTO "Question" ("Vote", "Content", "DateVote")
VALUES (
    1,
    'Какой знак зодиака?',
    '2022-09-25'
  );