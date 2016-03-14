import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.*;

public class DatabaseLogic {
    public DatabaseLogic() {
        this.connection = null;
        this.name = "newD";
        this.url = "jdbc:postgresql://localhost";
        this.port="5432";
        this.username = "postgres";
        this.password = "224244";
    }

    public void connect() throws SQLException{
        this.connection=DriverManager.getConnection(url+":"+port+"/", this.username, this.password);
    }

    public void connect(String url, String port, String user, String pw) throws SQLException{
        this.url = url;
        this.port= port;
        this.username = user;
        this.password = pw;
        this.connection=DriverManager.getConnection(url+":"+port+"/",this.username, this.password);

        this.connected=true;
    }

    public void disconnect() throws SQLException{
        this.connection.close();
        this.connected=false;
    }


    public void init() throws SQLException{
        if (!connected){
            connect();
            connected = true;
        }
        createStored();
    }

    public boolean isConnected(){
        return connected;
    }
    public String createDB() throws SQLException {
        Statement st=connection.createStatement();
        String msg="";
        try {
            st.executeUpdate("CREATE OR REPLACE FUNCTION f_create_db(name text)\n" +
                    "  RETURNS VOID AS\n" +
                    "$cr$\n" +
                    "BEGIN\n" +
                    "\n" +
                    "IF EXISTS (SELECT 1 FROM pg_database WHERE datname = name) THEN\n" +
                    "   RAISE NOTICE 'Database already exists'; \n" +
                    "ELSE\n" +
                    "   PERFORM dblink_connect('dbname='|| current_database() ||' host=127.0.0.1 port=5432 user=postgres password=224244');\n" +
                    "   PERFORM dblink_exec('CREATE DATABASE ' || name);\n" +
                    "END IF;\n" +
                    "\n" +
                    "END\n" +
                    "$cr$ LANGUAGE plpgsql;");
            CallableStatement callable = connection.prepareCall("{call f_create_db(?)}");
            callable.setString(1, name);
            callable.executeUpdate();
            try {
                connection.close();
                connection = DriverManager.getConnection(url + ":" + port + "/" + name.toLowerCase(), this.username, this.password);
                connected= true;
            }
            catch (Exception e){
                System.out.print(url + ":" + port + "/" + name.toLowerCase()+ this.username+ this.password);
                connection = DriverManager.getConnection(this.url+ ":" + port + "/"+name.toLowerCase() ,this.username, this.password);
                connected= true;
            }

            if (callable.getWarnings()==null){
                msg="Database "+name+" has been created";
                try {
                    createStored();
                    createTables();
                }
                catch (SQLException e) {

                    System.out.println(e.getMessage());
                    db = true;
                    return msg;
                }
                return msg;
            }
            else {
                db = true;
                try {
                    createStored();
                    createTables();
                }
                catch (SQLException e) {
                    msg=e.getMessage();
                    db = true;
                }
                return "Database "+name+" already exists";
            }

        }
        catch (SQLException e){
            if (!e.getMessage().contains(name.toLowerCase())) {
                msg = e.getMessage();
                return msg;
            }
            db = true;
            return "Database "+name+" already exists";
        }
    }

    public String createDB(String n) throws SQLException{
        this.name=n;
        return createDB();
    }

    public String getName(){
        return name;
    }

    public void deleteDB() throws SQLException {
        if (!db){
            return;
        }
        Statement statement;
        statement = connection.createStatement();
        statement.executeUpdate("DROP DATABASE " + this.name);
        disconnect();
        connect();
        System.out.println("Database " + this.name + "has been deleted");
    }

    public boolean hasDB(){
        return db;
    }

    private void createStored() throws SQLException{
        Statement statement;
        statement = connection.createStatement();
        statement.executeUpdate("CREATE OR REPLACE FUNCTION create_tables() RETURNS VOID\n " +
                "AS $$\n" +
                "BEGIN\n" +

                "   create table PILOT (\n" +
                "   id integer NOT NULL, \n" +
                "   name varchar(30) NOT NULL, \n" +
                "   crew varchar(30), \n" +
                "   hours_limit integer);\n" +
                "\n" +
                "alter table PILOT\n" +
                "add PRIMARY KEY (id);\n" +
                "\n" +

                "create table NAVIGATOR (\n" +
                "id integer, \n" +
                "name varchar(30), \n" +
                "crew varchar(30), \n" +
                "hours_limit integer);\n" +
                "\n" +
                "alter table NAVIGATOR\n" +
                "add PRIMARY KEY (id);\n" +
                "\n" +

                "create table TRIP (\n" +
                "id integer, \n" +
                "destination varchar(30), \n" +
                "crew varchar(30), \n" +
                "time_of_flight integer, \n" +
                "complexity_coef numeric(5,2)\n" +
                ");\n" +
                "\n" +
                "alter table TRIP\n" +
                "add PRIMARY KEY (id);\n" +
                "\n" +
                "\n" +

                "create table FLIGHT (\n" +
                "record_number integer, \n" +
                "date_of_flight date,\n" +
                " pilot_id integer references PILOT(id), \n" +
                " navigator_id integer references NAVIGATOR(id), \n" +
                " trip_id integer references TRIP(id),\n" +
                " number_of_flights integer,\n" +
                " number_of_hours integer);\n" +
                "\n" +
                "alter table FLIGHT\n" +
                "add PRIMARY KEY (record_number);" +

                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION get_table_pilot()\n" +
                "  RETURNS TABLE(" +
                "id integer , \n" +
                "name varchar(30) , \n" +
                "crew varchar(30), \n" +
                "lim integer)\n" +
                "AS $$\n" +
                "BEGIN\n" +
                "  RETURN QUERY\n" +
                "  SELECT *\n" +
                "  FROM pilot;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION get_table_navigator()\n" +
                "  RETURNS TABLE(" +
                "id integer , \n" +
                "name varchar(30) , \n" +
                "crew varchar(30), \n" +
                "lim integer"+
                ")\n" +
                "AS $$\n" +
                "BEGIN\n" +
                "  RETURN QUERY\n" +
                "  SELECT *\n" +
                "  FROM navigator;\n" +
                "END;\n" +
                "$$ \n" +
                "LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION get_table_trip()\n" +
                "  RETURNS TABLE(" +
                "id integer, \n" +
                "destination varchar(30), \n" +
                "crew varchar(30), \n" +
                "time_of_flight integer, \n" +
                "coef numeric(5,2)"+
                " )\n" +
                "AS\n" +
                " $$\n" +
                "BEGIN\n" +
                "  RETURN QUERY\n" +
                "  SELECT *\n" +
                "  FROM trip;\n" +
                "END;\n" +
                "$$\n" +
                " LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION get_table_flight()\n" +
                "  RETURNS TABLE (record_number integer, \n" +
                " date_of_flight date,\n" +
                " pilot_id integer , \n" +
                " navigator_id integer , \n" +
                " trip_id integer ,\n" +
                " number_of_flights integer,\n" +
                " number_of_hours integer)\n"+
                "AS\n $$\n" +
                "BEGIN\n" +
                "  RETURN QUERY\n" +
                "  SELECT *\n" +
                "  FROM flight;\n" +
                "END;\n" +
                "$$\n" +
                " LANGUAGE plpgsql;");


        statement.executeUpdate("CREATE OR REPLACE FUNCTION pilot_insert(\n" +
                "\n" +
                        "id integer , \n" +
                        "name varchar(30) , \n" +
                        "crew varchar(30), \n" +
                        "lim integer"+
                ")\n" +
                "  RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "  INSERT INTO PILOT VALUES (id, name, crew, lim);\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION navigator_insert(\n" +
                "\n" +
                "id integer , \n" +
                "name varchar(30) , \n" +
                "crew varchar(30), \n" +
                "lim integer"+
                ")\n" +
                "  RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "  INSERT INTO navigator VALUES (id, name, crew, lim);\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION trip_insert(\n" +
                "\n" +
                        "id integer, \n" +
                        "destination varchar(30), \n" +
                        "crew varchar(30), \n" +
                        "time_of_flight integer, \n" +
                        "coef numeric(5,2)"+
                ")\n" +
                "  RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "  INSERT INTO TRIP VALUES (id, destination, crew, time_of_flight,coef);\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION flight_insert(\n" +
                "\n" +
                        "record_number integer, \n" +
                        "date_of_flight date,\n" +
                        " pilot_id integer , \n" +
                        " navigator_id integer , \n" +
                        " trip_id integer ,\n" +
                        " number_of_flights integer,\n" +
                        " number_of_hours integer"+
                ")\n" +
                "  RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "  INSERT INTO flight VALUES (record_number, date_of_flight, pilot_id, navigator_id, trip_id, number_of_flights,number_of_hours);\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION clear(name TEXT )\n" +
                "  RETURNS VOID\n" +
                "AS\n" +
                "$clear$\n" +
                "BEGIN\n" +
                "IF EXISTS (SELECT 1 FROM information_schema.tables  WHERE table_name = lower(name)) THEN\n"+
                "  EXECUTE 'DELETE FROM ' || lower(name);\n" +
                "ELSE\n" +
                        "RAISE NOTICE 'no such table';\n"+
                "END IF;\n"+
                "END;\n" +
                "$clear$\n" +
                "LANGUAGE plpgsql VOLATILE;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION search(n TEXT)\n" +
                "  RETURNS TABLE(record integer, dat date, p_id integer, n_id integer, t_id integer, flights integer, hours integer, pilot text, nav text, dest text)\n" +
                "AS $BODY$\n" +
                "BEGIN\n" +
                "  RETURN QUERY\n" +
                "select f.record_number as \"record\", f.date_of_flight \"date\", p.id p_id, n.id nav_id, t.id t_id, f.number_of_flights \"flights\",\n" +
                        " f.number_of_hours hours, p.name \"pilot\", n.name nav, t.destination dest\n" +
                        " from flight f join pilot p on f.pilot_id=p.id \n" +
                        " join navigator n on f.navigator_id=n.id \n" +
                        " join trip t on f.trip_id=t.id\n" +
                        "where p.name=n;\n"+
                "END;\n" +
                "$BODY$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION change_record(rec integer, flights integer, hours integer)\n" +
                "  RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                "  UPDATE flight SET number_of_flights = flights " +
                "   where flight.record_number=rec;\n" +
                "  UPDATE flight SET number_of_hours = hours " +
                "   where flight.record_number=rec;\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");

        statement.executeUpdate("CREATE OR REPLACE FUNCTION delete_by_name(n TEXT)\n" +
                "  RETURNS VOID AS $$\n" +
                "BEGIN\n" +
                " delete from pilot\n" +
                "where name=n;" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;");
    }

    public void insertPilot(int id, String name, String crew, int limit) throws SQLException{
        CallableStatement st=connection.prepareCall("{call pilot_insert(?,?,?,?)}");
        st.setInt(1, id);
        st.setString(2, name);
        st.setString(3, crew);
        st.setInt(4, limit);
        st.executeUpdate();
    }

    public void insertNavigator(int id, String name, String crew, int limit) throws SQLException{
        CallableStatement st=connection.prepareCall("{call navigator_insert(?,?,?,?)}");
        st.setInt(1, id);
        st.setString(2, name);
        st.setString(3, crew);
        st.setInt(4, limit);
        st.executeUpdate();
    }

    public void insertTrip(int id, String dest, String crew, int duration, double coef) throws SQLException{
        CallableStatement st=connection.prepareCall("{call trip_insert(?,?,?,?,?)}");
        st.setInt(1, id);
        st.setString(2, dest);
        st.setString(3, crew);
        st.setInt(4, duration);
        st.setDouble(5, coef);
        st.executeUpdate();
    }
    public void insertFlight(int id, Date d, int pid, int nid, int tid, int flights, int hours) throws SQLException{
        CallableStatement st=connection.prepareCall("{call flight_insert(?,?,?,?,?,?,?)}");
        st.setInt(1, id);
        st.setDate(2,d);
        st.setInt(3,pid);
        st.setInt(4,nid);
        st.setInt(5,tid);
        st.setInt(6,flights);
        st.setInt(7,hours);
        st.executeUpdate();
    }

    public void  createTables() throws SQLException{
        if (cr) return;
        cr=true;
        CallableStatement st = connection.prepareCall("{call create_tables()}");
        st.executeUpdate();
    }

    public void clearTable(String n) throws  SQLException{
        CallableStatement st = connection.prepareCall("{call clear(?)}");
        st.setString(1,n);
        st.executeUpdate();
        if (st.getWarnings()==null){
            System.out.println("Table "+n+" has been deleted" );
        }
        else {
            System.out.println("There is no such table");
        }
    }

    public String printTable(String n) throws SQLException{
        PrintStream std = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        CallableStatement st;
        ResultSet set;

        switch (n.toLowerCase()){
            case "pilot":
                st = connection.prepareCall("{call get_table_pilot()}");
                set = st.executeQuery();

                System.out.println("\nThis is our table for pilots:");
                System.out.format("%7s%20s%20s%10s%n", "ID", "NAME", "CREW", "LIMIT");

                while (set.next()) {
                    System.out.format("%7s%20s%20s%10s%n",
                            set.getInt("id"), set.getString("name"),
                            set.getString("crew"), set.getInt("lim"));
                }
                break;
            case "navigator":
                st = connection.prepareCall("{call get_table_navigator()}");
                set = st.executeQuery();
                System.out.println("\nThis is our table for navigators:");
                System.out.format("%7s%20s%20s%10s%n", "ID", "NAME", "CREW", "LIMIT");
                while (set.next()) {
                    System.out.format("%5s%20s%20s%5s%n",
                            set.getInt("id"), set.getString("name"),
                            set.getString("crew"), set.getInt("lim"));
                }
                break;
            case "trip":
                st = connection.prepareCall("{call get_table_trip()}");
                set = st.executeQuery();
                System.out.println("\nThis is our table for trips:");
                System.out.format("%5s%20s%20s%15s%10s%n", "ID", "DESTINATION", "CREW", "TIME_OF_FLIGHT", "COMPLEXITY");
                while (set.next()) {
                    System.out.format("%5s%20s%20s%15s%10s%n",
                            set.getInt("id"), set.getString("destination"),
                            set.getString("crew"), set.getInt("time_of_flight"),set.getDouble("coef"));
                }
                break;
            case "flight":
                st = connection.prepareCall("{call get_table_trip()}");
                set = st.executeQuery();
                System.out.println("\nThis is our table for trips:");
                System.out.format("%6s%15s%7s%9s%7s%7s%7s%n", "RECORD", "DATE", "PILOT", "NAVIGATOR", "TRIP", "FLIGHTS", "HOURS");
                while (set.next()) {
                    System.out.format("%6s%15s%7s%9s%7s%7s%7s%n",
                            set.getInt("record_number"), set.getDate("date_of_flight"),
                            set.getInt("pilot_id"),set.getInt("navigator_id"),
                            set.getInt("trip_id"), set.getInt("number_of_flights"),
                            set.getInt("number_of_hours"));
                }
                break;
            default:
                System.out.println("There is no such table");
                break;
        }

        System.setOut(std);
        return baos.toString();
    }

    public String search(String n) throws SQLException {

        PrintStream std = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);
        CallableStatement st = connection.prepareCall("{call search (?)}");
        st.setString(1, n);
        ResultSet set = st.executeQuery();
        if (!set.isBeforeFirst() ) {
            System.out.println("No data found");
            System.setOut(std);
            return baos.toString();
        }
        System.out.println("\nThis is our table for trips:");
        System.out.format("%6s%15s%5s%5s%5s%7s%5s%12s%12s%10s%n", "RECORD", "DATE", "P_ID",  "N_ID",  "T_ID",  "FLIGHTS", "HOURS", "PILOT", "NAVIGATOR", "DESTINATION");

        while (set.next()) {
            System.out.format("%6s%15s%5s%5s%5s%7s%5s%12s%12s%10s%n",
                    set.getInt("record"), set.getDate("dat"),
                    set.getInt("p_id"),set.getInt("n_id"),
                    set.getInt("t_id"), set.getInt("flights"),
                    set.getInt("hours"), set.getString(8),
                    set.getString(9), set.getString(10));
        }

        System.setOut(std);
        return baos.toString();
    }
    public void updateRecord(int record, int flights, int hours) throws SQLException{
        CallableStatement st = connection.prepareCall("{call change_record(?,?,?)}");
        st.setInt(1,record);
        st.setInt(2,flights);
        st.setInt(3,hours);
        st.executeUpdate();
    }

    public void deletePilot(String n) throws SQLException{
        CallableStatement st = connection.prepareCall("{call delete_by_name(?)}");
        st.setString(1,n);
        st.executeUpdate();
        if (st.getWarnings()!=null){
            System.out.println("No such pilot");
        }
    }
    public boolean isCreated(){
        return cr;
    }
//fields
    boolean cr=false;
    boolean db=false;
    boolean connected=false;
    protected Connection connection = null;
    protected String url;
    protected String port;
    protected String username;
    protected String password;
    protected String name;
}
