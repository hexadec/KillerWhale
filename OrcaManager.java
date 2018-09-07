package hu.hexadec.killerwhale;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public final class OrcaManager {

    private Context context;
    private List<Orca> orcas;

    /**
     * Constructor
     *
     * @param context context to work with while reading from assets, and starting activities
     *                be careful not to use this object if its context does not exist anymore
     * @throws FileNotFoundException if orcas.csv is not placed in assets folder, or an error occurs while parsing
     */
    public OrcaManager(Context context) throws FileNotFoundException {
        this(context, null);
    }

    /**
     * @param context       context to work with while reading from assets, and starting activities
     *                      be careful not to use this object if its context does not exist anymore
     * @param excludeVendor String array containing vendor names who should be excluded while doing any work
     *                      equivalent of deleting all lines containing given vendor names
     * @throws FileNotFoundException if orcas.csv is not placed in assets folder, or an error occurs while parsing
     */
    public OrcaManager(Context context, String[] excludeVendor) throws FileNotFoundException {
        this.context = context;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("orcas.csv"), "UTF-8"));
            orcas = new ArrayList<>();
            String line;
            boolean first = true;
            readerloop:
            while ((line = reader.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] elements = line.split(",");
                Orca o = new Orca();
                o.settings = new ComponentName(elements[0], elements[1]);
                o.vendor = elements[4];
                if (excludeVendor != null) {
                    for (String vendor : excludeVendor) {
                        if (o.vendor.equalsIgnoreCase(vendor))
                            continue readerloop;
                    }
                }
                o.MinAPI = Integer.valueOf(elements[2]);
                o.MaxAPI = Integer.valueOf(elements[3]);
                orcas.add(o);
            }
        } catch (IOException e) {
            throw new FileNotFoundException();
        } catch (NumberFormatException|ArrayIndexOutOfBoundsException e) {
            throw new FileNotFoundException("Invalid file!");
        } catch (Exception e) {
            throw new FileNotFoundException(e.getMessage());
        }
    }

    /**
     * @return true if the given vendor and API level is in the file
     * false otherwise
     */
    public boolean hasVendorOptimization() {
        for (Orca o : orcas) {
            if (o.matchesDevice()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Start all intents matching vendor name and API level
     *
     * @throws ActivityNotFoundException
     * @throws IllegalArgumentException
     */
    public void startIntents() throws ActivityNotFoundException, IllegalArgumentException {
        Intent[] intents = getIntents();
        if (intents.length == 0) throw new ActivityNotFoundException("No intents found");
        context.startActivities(getIntents());
    }

    /**
     * Start all intents matching vendor name and API level
     * Exceptions are handled internally
     *
     * @return number of activities started
     */
    public int startEachIntent() {
        Intent[] intents = getIntents();
        int activitiesStarted = 0;
        for (Intent i : intents) {
            try {
                context.startActivity(i);
                activitiesStarted++;
            } catch (ActivityNotFoundException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return activitiesStarted;
    }

    /**
     * @return all intents matching vendor name and API level
     */
    public Intent[] getIntents() {
        List<Intent> intentList = new ArrayList<>();
        for (Orca o : orcas) {
            if (o.matchesDevice()) {
                intentList.add(new Intent().setComponent(o.settings)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        return intentList.toArray(new Intent[intentList.size()]);
    }
}

final class Orca {
    int MinAPI;
    int MaxAPI;
    String vendor;
    ComponentName settings;

    boolean matchesDevice() {
        return (vendor.equalsIgnoreCase(Build.MANUFACTURER) &&
                MinAPI <= Build.VERSION.SDK_INT && (MaxAPI >= Build.VERSION.SDK_INT || MaxAPI == 0));
    }
}

