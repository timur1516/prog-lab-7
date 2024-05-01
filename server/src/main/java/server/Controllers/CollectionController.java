package server.Controllers;

import common.Collection.*;
import common.utils.CommonConstants;
import common.Exceptions.InvalidDataException;
import common.Validators.WorkerValidators;
import server.DB.DBQueries;
import server.utils.ServerLogger;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class which completes all operations with Collection of workers
 *
 */
public class CollectionController {
    private static CollectionController COLLECTION_CONTROLLER = null;

    public static synchronized CollectionController getInstance(){
        if(COLLECTION_CONTROLLER == null){
            COLLECTION_CONTROLLER = new CollectionController();
        }
        return COLLECTION_CONTROLLER;
    }

    /**
     * Collection of workers, which we operate on
     */
    private PriorityQueue<Worker> collection;
    /**
     * Collection's creation date
     * <p>In fact it is equal to CollectionManager object creation date
     */
    private final LocalDateTime creationDate;
    /**
     * Boolean value which is true if collection was change after last saving or loading from data file
     */
    private boolean changeFlag;

    /**
     * CollectionController constructor
     * <p>Completes initialization of collection, generate creationDate and set changeFlag to false
     */
    private CollectionController() {
        collection = new PriorityQueue<>();
        this.creationDate = LocalDateTime.now();
        this.changeFlag = false;
    }

    /**
     * Method to check if collection contains valid values
     * <p>Used to validate input collection from data file
     * <p>Firstly it checks if all id are unique
     * <p>Then it validate all fields using WorkerValidator
     * @return
     */
    public static boolean isValid(PriorityQueue<Worker> collection){
        Set<Long> idSet = collection.stream().map(Worker::getId).collect(Collectors.toSet());
        if(idSet.size() != collection.size()) return false;
        for(Worker worker : collection){
            try {
                WorkerValidators.workerValidator.validate(worker);
            } catch (InvalidDataException e){
                return false;
            }
        }
        return true;
    }

    /**
     * Method to get the collection
     *
     * @return Collection of workers
     */
    public PriorityQueue<Worker> getCollection() {
        return this.collection;
    }

    /**
     * Method to get the creation date of the class object
     *
     * @return The creation date of the collection
     */
    public LocalDateTime getCreationDate(){
        return this.creationDate;
    }

    /**
     * This method check if collection contain any element with id equal to given
     * @param id to compare with
     * @return true if element was found, else false
     */
    public boolean containsId(long id){
        if(this.collection.isEmpty()) return false;
        return this.collection.stream().anyMatch(worker -> worker.getId() == id);
    }

    /**
     * Method to get information about collection (type of elements, creation date, collection size)
     * @return Formatted string
     */
    public String getInfo() {
        return "Type: " + this.collection.getClass().getName() +
            "\nCreation date: " + this.creationDate.format(CommonConstants.formatter) +
            "\nSize: " + this.collection.size();
    }

    private void loadWorkerToStatement(Worker newWorker, PreparedStatement statement) throws SQLException {
        String name = newWorker.getName();
        double x = newWorker.getCoordinates().getX();
        double y = newWorker.getCoordinates().getY();
        Integer salary = newWorker.getSalary();
        Timestamp startDate = Timestamp.valueOf(newWorker.getStartDate());
        Timestamp endDate = newWorker.getEndDate() == null ? null : Timestamp.valueOf(newWorker.getEndDate());
        String status = String.valueOf(newWorker.getStatus());
        Long height = null;
        String eyeColor = null;
        String nationality = null;
        if(newWorker.getPerson() != null) {
            height = newWorker.getPerson().getHeight();
            eyeColor = newWorker.getPerson().getEyeColor() == null ? null : String.valueOf(newWorker.getPerson().getEyeColor());
            nationality = newWorker.getPerson().getNationality() == null ? null : String.valueOf(newWorker.getPerson().getNationality());
        }

        statement.setString(1, name);
        statement.setDouble(2, x);
        statement.setDouble(3, y);
        statement.setInt(4, salary);
        statement.setTimestamp(5, startDate);
        statement.setTimestamp(6, endDate);
        statement.setString(7, status);
        statement.setObject(8, height, Types.BIGINT);
        statement.setString(9, eyeColor);
        statement.setString(10, nationality);
    }

    /**
     * Add new object to collection
     *
     * @param newWorker Object to add
     */
    public void add(Worker newWorker, String username) throws SQLException {
        PreparedStatement add_command_query = DBQueries.ADD_COMMAND();

        loadWorkerToStatement(newWorker, add_command_query);
        add_command_query.setString(11, username);

        add_command_query.execute();
        add_command_query.close();
        loadCollection();
    }

    /**
     * Updates value of collection element by it's id
     *
     * @param id Element's id
     * @param newWorker New value for the element
     */
    public void update(long id, Worker newWorker, String username) throws SQLException {
        PreparedStatement update_command_query = DBQueries.UPDATE_COMMAND();

        loadWorkerToStatement(newWorker, update_command_query);
        update_command_query.setString(11, username);
        update_command_query.setLong(12, id);

        update_command_query.execute();
        update_command_query.close();
        loadCollection();
    }

    /**
     * Removes element with given id from collection
     *
     * @param id Element's id
     */
    public void removeById(long id, String username) throws SQLException {
        PreparedStatement remove_by_id_query = DBQueries.REMOVE_BY_ID_COMMAND();

        remove_by_id_query.setString(1, username);
        remove_by_id_query.setLong(2, id);

        remove_by_id_query.execute();
        remove_by_id_query.close();
        loadCollection();
    }

    /**
     * Clear collection
     */
    public void clear(String username) throws SQLException {
        PreparedStatement clear_command_query = DBQueries.CLEAR_COMMAND();

        clear_command_query.setString(1, username);

        clear_command_query.execute();
        clear_command_query.close();
        loadCollection();
    }

    /**
     * Removes the first element in the collection
     */
    public void removeFirst(String username) throws SQLException {
        PreparedStatement remove_first_command_query = DBQueries.REMOVE_FIRST_COMMAND();
        remove_first_command_query.setString(1, username);
        remove_first_command_query.execute();
        remove_first_command_query.close();
        loadCollection();
    }

    /**
     * Removes all elements which are greater that given
     * @param worker Element to compare with
     * @return Number of deleted elements
     */
    public int removeGreater(Worker worker, String username) throws SQLException {
        int oldSize = this.collection.size();

        PreparedStatement remove_greater_command_query = DBQueries.REMOVE_GREATER_COMMAND();
        remove_greater_command_query.setString(1, username);
        remove_greater_command_query.setString(2, worker.getName());
        remove_greater_command_query.execute();
        remove_greater_command_query.close();
        loadCollection();

        return oldSize - this.collection.size();
    }

    /**
     * Removes all elements which are lowers than given
     *
     * @param worker Element to compare with
     * @return Number of deleted elements
     */
    public int removeLower(Worker worker, String username) throws SQLException {
        int oldSize = this.collection.size();

        PreparedStatement remove_lower_command_query = DBQueries.REMOVE_LOWER_COMMAND();
        remove_lower_command_query.setString(1, username);
        remove_lower_command_query.setString(2, worker.getName());
        remove_lower_command_query.execute();
        remove_lower_command_query.close();
        loadCollection();

        return oldSize - this.collection.size();
    }

    /**
     * Method to get worker with minimal salary
     *
     * @return Worker whose salary is minimal
     */
    public Worker getMinBySalary(){
        return this.collection
                .stream()
                .min(Comparator.comparing(Worker::getSalary))
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Method to get all workers whose endDate is less that given
     *
     * @param endDate Date to compare with
     * @return List of workers
     */
    public List<Worker> getLessThanEndDate(LocalDateTime endDate){
       return this.collection
                .stream()
                .filter(worker1 -> (!Objects.isNull(worker1.getEndDate()) && worker1.getEndDate().isBefore(endDate)))
                .sorted().toList();
    }

    /**
     * Method to get salaries of all workers in descending order
     *
     * @return List of salaries
     */
    public List<Integer> getDescendingSalaries(){
        return this.collection
                .stream()
                .map(Worker::getSalary)
                .sorted(Comparator.reverseOrder()).toList();
    }

    /**
     * Method to load collection from SQL database
     * <p>Before saving, validation of loaded collection is completed
     * @throws SQLException
     */
    public void loadCollection() throws SQLException {
        PreparedStatement get_collection_query = DBQueries.GET_COLLECTION();
        ResultSet resultSet = get_collection_query.executeQuery();

        PriorityQueue<Worker> data = new PriorityQueue<>();

        while(resultSet.next()){
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            double x = resultSet.getDouble("x");
            double y = resultSet.getDouble("y");
            ZonedDateTime creationDate = resultSet.getObject("creationDate", OffsetDateTime.class).toZonedDateTime();
            Integer salary = resultSet.getInt("salary");
            LocalDateTime startDate = resultSet.getTimestamp("startDate").toLocalDateTime();
            LocalDateTime endDate = resultSet.getTimestamp("endDate") == null ? null : resultSet.getTimestamp("endDate").toLocalDateTime();
            Status status = Status.valueOf(resultSet.getString("status"));
            Long height = resultSet.getLong("height");
            Color eyeColor = resultSet.getString("eyeColor") == null ? null : Color.valueOf(resultSet.getString("eyeColor"));
            Country nationality = resultSet.getString("nationality") == null ? null : Country.valueOf(resultSet.getString("nationality"));

            Coordinates coordinates = new Coordinates(x, y);
            Person person = height == 0 ? null : new Person(height, eyeColor, nationality);
            Worker worker = new Worker(id, name, coordinates, creationDate, salary, startDate, endDate, status, person);
            data.add(worker);
        }
        resultSet.close();
        get_collection_query.close();

        if(isValid(data)) {
            collection = data;
            ServerLogger.getInstace().info("Collection have been loaded successfully!");
        }
        else{
            ServerLogger.getInstace().error("Collection was not loaded! Not valid data!");
        }
    }

    public boolean checkAccess(long id, String username) throws SQLException {
        PreparedStatement check_access_query = DBQueries.CHECK_ACCESS();
        check_access_query.setLong(1, id);
        check_access_query.setString(2, username);
        ResultSet resultSet = check_access_query.executeQuery();
        resultSet.next();
        return resultSet.getBoolean(1);
    }
}
