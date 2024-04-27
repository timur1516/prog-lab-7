package server.Controllers;

import common.Collection.*;
import common.Constants;
import common.Exceptions.InvalidDataException;
import common.Validators.WorkerValidators;
import server.DBQueries;
import server.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static server.Main.logger;

/**
 * Class which completes all operations with Collection of workers
 *
 */
public class CollectionController {
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
    public CollectionController() {
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
     * Method to check if current collection isn't saved in data file
     * @return boolean value
     */
    public boolean wasChanged(){
        return this.changeFlag;
    }

    /**
     * Method to set changeFlag to false value
     */
    public void removeChangeFlag(){
        this.changeFlag = false;
    }

    /**
     * Method to generate unique id for new element of collection
     * <p>It gets the maximum id in current collection and then increments it
     * @return id
     */
    public long generateId(){
        if(this.collection.isEmpty()) return 1;
        return this.collection
                .stream()
                .map(Worker::getId)
                .max(Long::compareTo).get() + 1;
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
            "\nCreation date: " + this.creationDate.format(Constants.formatter) +
            "\nSize: " + this.collection.size();
    }

    /**
     * Add new object to collection
     *
     * @param newWorker Object to add
     */
    public void add(Worker newWorker){
        newWorker.setId(this.generateId());
        newWorker.setCreationDate(ZonedDateTime.now());

        this.collection.add(newWorker);

        this.changeFlag = true;
    }

    /**
     * Updates value of collection element by it's id
     *
     * @param id Element's id
     * @param newWorker New value for the element
     */
    public void update(long id, Worker newWorker){
        removeById(id);
        newWorker.setId(id);
        newWorker.setCreationDate(ZonedDateTime.now());
        collection.add(newWorker);
    }

    /**
     * Removes element with given id from collection
     *
     * @param id Element's id
     */
    public void removeById(long id){
        this.collection.removeIf(worker -> worker.getId() == id);

        this.changeFlag = true;
    }

    /**
     * Clear collection
     */
    public void clear(){
        this.collection.clear();
        this.changeFlag = true;
    }

    /**
     * Removes the first element in the collection
     */
    public void removeFirst(){
        this.collection.poll();

        this.changeFlag = true;
    }

    /**
     * Removes all elements which are greater that given
     * @param worker Element to compare with
     * @return Number of deleted elements
     */
    public int removeGreater(Worker worker){
        int oldSize = this.collection.size();
        this.collection.removeIf(worker1 -> worker1.compareTo(worker) > 0);

        this.changeFlag = true;

        return oldSize - this.collection.size();
    }

    /**
     * Removes all elements which are lowers than given
     *
     * @param worker Element to compare with
     * @return Number of deleted elements
     */
    public int removeLower(Worker worker){
        int oldSize = this.collection.size();
        this.collection.removeIf(worker1 -> worker1.compareTo(worker) < 0);

        this.changeFlag = true;

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
        ResultSet resultSet = DBQueries.GET_COLLECTION.executeQuery();

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
        DBQueries.GET_COLLECTION.close();

        if(isValid(data)) {
            collection = data;
            logger.info("Collection have been loaded successfully!");
        }
        else{
            logger.error("Collection was not loaded! Not valid data!");
        }
    }
}
