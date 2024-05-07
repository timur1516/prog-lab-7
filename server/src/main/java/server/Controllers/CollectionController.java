package server.Controllers;

import common.Collection.*;
import common.utils.CommonConstants;
import common.Exceptions.InvalidDataException;
import common.Validators.WorkerValidators;
import server.DB.DBQueriesExecutors;
import server.utils.ServerLogger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton Class which completes all operations with Collection of workers
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
     * CollectionController constructor
     * <p>Completes initialization of collection, generate creationDate and set changeFlag to false
     */
    private CollectionController() {
        collection = new PriorityQueue<>();
        this.creationDate = LocalDateTime.now();
    }

    /**
     * Method to check if collection contains valid values
     * <p>Used to validate input collection from data file
     * <p>Firstly it checks if all id are unique
     * <p>Then it validate all fields using WorkerValidator
     * @return Boolean value with result
     */
    private static boolean isValid(PriorityQueue<Worker> collection){
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

    /**
     * Add new object to collection
     *
     * @param newWorker Object to add
     */
    public void add(Worker newWorker, String username) throws SQLException {
        DBQueriesExecutors.addCommandExecutor(newWorker, username);
        loadCollection();
    }

    /**
     * Updates value of collection element by it's id
     *
     * @param id Element's id
     * @param newWorker New value for the element
     */
    public void update(long id, Worker newWorker, String username) throws SQLException {
        DBQueriesExecutors.updateCommandExecutor(newWorker, id, username);
        loadCollection();
    }

    /**
     * Removes element with given id from collection
     *
     * @param id Element's id
     */
    public void removeById(long id, String username) throws SQLException {
        DBQueriesExecutors.removeByIdCommandExecutor(id, username);
        loadCollection();
    }

    /**
     * Clear collection
     */
    public void clear(String username) throws SQLException {
        DBQueriesExecutors.clearCommandExecutor(username);
        loadCollection();
    }

    /**
     * Removes the first element in the collection
     */
    public void removeFirst(String username) throws SQLException {
        DBQueriesExecutors.removeFirstCommandExecutor(username);
        loadCollection();
    }

    /**
     * Removes all elements which are greater that given
     * @param worker Element to compare with
     * @return Number of deleted elements
     */
    public int removeGreater(Worker worker, String username) throws SQLException {
        int oldSize = this.collection.size();

        DBQueriesExecutors.removeGreaterCommandExecutor(worker, username);
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

        DBQueriesExecutors.removeLowerCommandExecutor(worker, username);
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
     * <>Before saving, validation of loaded collection is completed
     * @throws SQLException If an error occurred while working with database
     */
    public void loadCollection() throws SQLException {
        PriorityQueue<Worker> data = DBQueriesExecutors.getCollectionExecutor();

        if(isValid(data)) {
            collection = data;
            ServerLogger.getInstance().info("Collection have been loaded successfully!");
        }
        else{
            ServerLogger.getInstance().error("Collection was not loaded! Not valid data!");
        }
    }

    /**
     * Method to check if user has rights to modify element with give id
     * @param id Element to check
     * @param username Information about user
     * @return Boolean value with result
     * @throws SQLException If an error occurred while working with database
     */
    public boolean checkAccess(long id, String username) throws SQLException {
        return DBQueriesExecutors.checkAccessExecutor(id, username);
    }

    /**
     * Method to get collection as formated string
     * @return String with collection
     */
    public String getStringCollection(){
        StringBuilder result = new StringBuilder();
        for(Worker worker : collection.stream().sorted().toList()) {
            result.append(worker.toString()).append("\n");
        }
        return result.toString();
    }

    /**
     * Method to check if collection is empty
     * @return Boolean value with result
     */
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }
}
