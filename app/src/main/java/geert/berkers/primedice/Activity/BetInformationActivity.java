package geert.berkers.primedice.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import geert.berkers.primedice.Data.Bet;
import geert.berkers.primedice.R;

/**
 * Primedice Application Created by Geert on 27-1-2016.
 */
public class BetInformationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bet_information_layout);

        TextView txtUsername = (TextView) findViewById(R.id.txtUsername);
        TextView txtRoll = (TextView) findViewById(R.id.txtRoll);
        TextView txtServerseed = (TextView) findViewById(R.id.txtServerseed);
        TextView txtClientseed = (TextView) findViewById(R.id.txtClientseed);
        TextView txtTime = (TextView) findViewById(R.id.txtTimeOfBet);
        TextView txtWagered = (TextView) findViewById(R.id.txtWagered);
        TextView txtPayout = (TextView) findViewById(R.id.txtPayout);
        TextView txtBetGame = (TextView) findViewById(R.id.txtBetGame);
        TextView txtBetProtif = (TextView) findViewById(R.id.txtBetProfit);

        Bundle b = getIntent().getExtras();
        try {
            Bet bet = b.getParcelable("bet");

            if (bet != null) {
                txtUsername.setText(bet.getPlayer());
                String roll = String.valueOf(bet.getRoll());
                if(roll.substring(roll.indexOf(".")).length() == 2){
                    roll = roll + "0";
                }
                txtRoll.setText(roll);
                txtServerseed.setText(bet.getServerseed());
                String clientSeed= bet.getClientseed() + "-" + bet.getNonce();
                txtClientseed.setText(clientSeed);
                txtTime.setText(bet.getTimeOfBet());
                txtWagered.setText(bet.getWagered());
                String payoutString = bet.getPayout() + "x";
                txtPayout.setText(payoutString);
                txtBetGame.setText(bet.getBetGame());
                txtBetProtif.setText(bet.getProfitString());

                boolean win = bet.getWinOrLose();
                if (!win) {
                    txtBetProtif.setTextColor(Color.RED);
                } else {
                    txtBetProtif.setTextColor(Color.argb(255, 0, 100, 0));
                }

                setTitle("BET #" + bet.getIDString().replace(",","") + " INFO");
            } else throw new Exception("Bet is null");
        } catch (Exception ex) {
            Toast.makeText(this.getApplicationContext(),"Bet not found!", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
            this.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
