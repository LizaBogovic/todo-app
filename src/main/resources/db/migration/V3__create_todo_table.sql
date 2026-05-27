-- 1. Create the sequence for the primary key (Optional, but good for explicit control)
CREATE SEQUENCE IF NOT EXISTS todo_id_seq START WITH 1 INCREMENT BY 1;

-- 2. Create the todo table
CREATE TABLE todo (
                       id BIGINT DEFAULT nextval('todo_id_seq') NOT NULL,
                       title VARCHAR(100) NOT NULL,
                       description TEXT,
                       priority VARCHAR(20) NOT NULL,
                       category VARCHAR(20) NOT NULL,
                       deadline DATE NOT NULL,

    -- Constraints
                       CONSTRAINT pk_todo PRIMARY KEY (id),

    -- Data integrity safety net: Ensure enums only accept valid strings
                       CONSTRAINT chk_todo_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
                       CONSTRAINT chk_todo_category CHECK (category IN ('PERSONAL', 'WORK', 'URGENT', 'HEALTH'))
);

-- 3. Add an index on deadline for faster querying/sorting (e.g., getting upcoming todo)
CREATE INDEX idx_todo_deadline ON todo(deadline);