import sys
import os
import json
import traceback

# ----------------------------
# BASIC ARG VALIDATION
# ----------------------------
if len(sys.argv) < 3:
    print("INVALID_ARGUMENTS")
    sys.exit(1)

ACTION = sys.argv[1]
TABLE = sys.argv[2]
# print(f"ACTION: {ACTION}\n")
# print(f"TABLE: {TABLE}\n")
# ----------------------------
# DIRECTORIES
# ----------------------------
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

SCHEMA_DIR = os.path.join(BASE_DIR, "..", "schema")
DATA_DIR = os.path.join(BASE_DIR, "..", "data")

os.makedirs(SCHEMA_DIR, exist_ok=True)
os.makedirs(DATA_DIR, exist_ok=True)

schema_file = os.path.join(SCHEMA_DIR, f"{TABLE}.schema.json")
data_file = os.path.join(DATA_DIR, f"{TABLE}.json")


# ----------------------------
# HELPERS
# ----------------------------
def load_schema():
    if not os.path.exists(schema_file):
        print("SCHEMA_NOT_FOUND")
        sys.exit(1)
    with open(schema_file, "r") as f:
        return json.load(f)


def load_data():
    if not os.path.exists(data_file):
        return []
    with open(data_file, "r") as f:
        return json.load(f)


def save_data(data):
    with open(data_file, "w") as f:
        json.dump(data, f, indent=2)


def cast(value, typ):
    try:
        if typ == "int":
            return int(value)
        if typ == "float":
            return float(value)
        return str(value)
    except ValueError:
        print(f"TYPE_CAST_ERROR: {value} -> {typ}")
        sys.exit(1)


# ----------------------------
# MAIN LOGIC
# ----------------------------
try:

    # 1️⃣ CREATE TABLE
    if ACTION == "create_table":
        if len(sys.argv) < 4:
            print("NO_COLUMNS_PROVIDED")
            sys.exit(1)

        columns = sys.argv[3].split(",")

        schema = []
        for col in columns:
            if ":" not in col:
                print("INVALID_COLUMN_FORMAT")
                sys.exit(1)
            name, typ = col.split(":")
            schema.append({"name": name, "type": typ})

        with open(schema_file, "w") as f:
            json.dump(schema, f, indent=2)

        if not os.path.exists(data_file):
            save_data([])

        print("TABLE_CREATED")

    # 2️⃣ INSERT WITH AUTO ID
    elif ACTION == "insert":
        if len(sys.argv) < 4:
            print("NO_VALUES_PROVIDED")
            sys.exit(1)

        values = sys.argv[3].split(",")
        schema = load_schema()
        data = load_data()

        expected = len(schema) - 1  # exclude id
        if len(values) != expected:
            print("VALUE_COUNT_MISMATCH")
            sys.exit(1)

        record = {}
        value_index = 0

        for col in schema:
            if col["name"] == "id":
                last_id = data[-1]["id"] if data else 0
                record["id"] = last_id + 1
            else:
                record[col["name"]] = cast(values[value_index], col["type"])
                value_index += 1

        data.append(record)
        save_data(data)

        print("ROW_INSERTED")

    # 3️⃣ FIND (WHERE key=value)
    elif ACTION == "find":
        print("FIND_ACTION_CALLED")
        if len(sys.argv) < 4 or "=" not in sys.argv[3]:
            print("INVALID_CONDITION")
            sys.exit(1)

        condition = sys.argv[3]
        key, value = condition.split("=", 1)

        schema = load_schema()
        data = load_data()

        # Detect column type
        for col in schema:
            if col["name"] == key:
                value = cast(value, col["type"])
                break

        found = False
        for row in data:
            if row.get(key) == value:
                print(json.dumps(row, indent=2))
                found = True

        if not found:
            print("NOT_FOUND")

    else:
        print("UNKNOWN_ACTION")
        sys.exit(1)

except Exception:
    print("PYTHON_INTERNAL_ERROR")
    traceback.print_exc()
    sys.exit(1)
