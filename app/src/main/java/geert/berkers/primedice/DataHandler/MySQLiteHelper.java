package geert.berkers.primedice.DataHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import geert.berkers.primedice.Data.Bet;

/**
 * Primedice Application Created by Geert on 23-9-2015
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    // Datbase information
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_BET = "Bet";
    private static final String DATABASE_NAME = "MyBetsDB";

    // Bet Table Columns names
    private static final String ID = "id";
    private static final String USER = "user";
    private static final String PROFIT = "profit";
    private static final String WIN = "win";
    private static final String AMOUNT = "amount";
    private static final String CONDITION = "condition";
    private static final String TARGET = "target";
    private static final String ROLL = "roll";
    private static final String NONCE = "nonce";
    private static final String CLIENT = "client";
    private static final String MULTIPLIER = "multiplier";
    private static final String TIMESTAMP = "timestamp";
    private static final String SERVER = "server";

    private static final String[] COLUMNS = {ID, USER, PROFIT, WIN, AMOUNT, CONDITION, TARGET, ROLL, NONCE, CLIENT, MULTIPLIER, TIMESTAMP, SERVER};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BET_TABLE = "CREATE TABLE Bet( " +
                "id VARCHAR(15) PRIMARY KEY, " +
                "user VARCHAR(20), " +
                "profit VARCHAR(15), " +
                "win VARCHAR(1), " +
                "amount VARCHAR(15), " +
                "condition VARCHAR(1), " +
                "target VARCHAR(5), " +
                "roll VARCHAR(5), " +
                "nonce VARCHAR(10), " +
                "client VARCHAR(50), " +
                "multiplier VARCHAR(5), " +
                "timestamp VARCHAR(25), " +
                "server VARCHAR(25)" +
                ")";

        db.execSQL(CREATE_BET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Bet");
        this.onCreate(db);
    }

    // Add a bet
    public void addBet(Bet bet) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(ID, bet.getIDString());
        values.put(USER, bet.getPlayer());
        values.put(PROFIT, bet.getProfitString());

        if (bet.getWinOrLose()) {
            values.put(WIN, "Y");
        } else {
            values.put(WIN, "N");
        }

        values.put(AMOUNT, bet.getWagered());
        values.put(CONDITION, bet.getBetGame().substring(0, 1));
        values.put(TARGET, bet.getBetGame().substring(1));
        values.put(ROLL, bet.getRoll());
        values.put(NONCE, bet.getNonce());
        values.put(CLIENT, bet.getClientseed());
        values.put(MULTIPLIER, bet.getPayout());
        values.put(TIMESTAMP, bet.getTimeOfBet());
        values.put(SERVER, bet.getServerseed());

        // Insert bet in table
        try {
            db.insert(TABLE_BET, null, values);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        db.close();
    }

    // Get all bets from one user
    public ArrayList<Bet> getAllBetsFromUser(String username) {
        ArrayList<Bet> betList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_BET, COLUMNS, " user = ?", new String[]{username}, null, null, null, null);

        // Check each row and create Bet
        Bet bet;
        if (cursor.moveToFirst()) {
            do {
                long id = Long.valueOf(cursor.getString(0).replace(",", ""));
                String user = cursor.getString(1);
                int profit = (int) (Double.valueOf(cursor.getString(2)) * 100000000);
                String win = cursor.getString(3);
                int amount = (int) (Double.valueOf(cursor.getString(4)) * 100000000);
                String condition = cursor.getString(5);
                Double target = Double.valueOf(cursor.getString(6));
                Double roll = Double.valueOf(cursor.getString(7));
                int nonce = Integer.valueOf(cursor.getString(8));
                String client = cursor.getString(9);
                Double multiplier = Double.valueOf(cursor.getString(10));
                String timestamp = cursor.getString(11);
                String server = cursor.getString(12);

                bet = new Bet(id, user, profit, win, amount, condition, target, roll, nonce, client, multiplier, timestamp, server);

                betList.add(0, bet);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return betList;
    }

    // Delete a bet
    public void deleteBet(Bet bet) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BET, ID + " = ?", new String[]{bet.getIDString()});
        db.close();
    }
}