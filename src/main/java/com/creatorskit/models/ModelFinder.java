package com.creatorskit.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelFinder
{
    public static ModelStats[] findModelsForItems(boolean groundItem, boolean maleItem, int... itemIdFull)
    {
        Pattern recolFrom = Pattern.compile("recol\\ds=.+");
        Pattern recolTo = Pattern.compile("recol\\dd=.+");

        ArrayList<Integer> itemList = new ArrayList<>();
        ArrayList<Integer> kitList = new ArrayList<>();
        for (int i : itemIdFull)
        {
            if (i == 0)
            {
                continue;
            }

            if (i < 256)
            {
                kitList.add(i);
                continue;
            }

            itemList.add(i);
        }

        ArrayList<ModelStats> modelStatsItems = new ArrayList<>();
        //for modelIds

        int[] itemId = new int[itemList.size()];
        for (int i = 0; i < itemList.size(); i++)
        {
            itemId[i] = itemList.get(i);
        }

        try
        {
            URL url = new URL("https://gitlab.com/waliedyassen/cache-dumps/-/raw/master/dump.obj");
            URLConnection connection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = "";
            Pattern[] patterns = new Pattern[itemId.length];
            System.out.println(itemId.length);

            for (int i = 0; i < itemId.length; i++)
            {
                int item = itemId[i];
                Pattern itemPattern = Pattern.compile("\\[.+_" + item + "]");
                patterns[i] = itemPattern;
            }

            while ((s = br.readLine()) != null)
            {
                for (int i = 0; i < patterns.length; i++)
                {
                    Pattern pattern = patterns[i];
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches())
                    {
                        System.out.println("Match found: " + s);
                        int[] modelIds = new int[3];
                        ArrayList<Integer> recolourFrom = new ArrayList<>();
                        ArrayList<Integer> recolourTo = new ArrayList<>();


                        String string = "";
                        while (!(string = br.readLine()).equals(""))
                        {
                            if (groundItem && string.startsWith("model="))
                            {
                                String[] split = string.split("_");
                                modelIds[0] = Integer.parseInt(split[split.length - 1]);
                            }
                            else
                            {
                                if (maleItem && string.startsWith("manwear="))
                                {
                                    String replaced = string.replace(",", "_");
                                    String[] split = replaced.split("_");
                                    modelIds[0] = Integer.parseInt(split[split.length - 2]);
                                    continue;
                                }

                                if (maleItem && string.startsWith("manwear2="))
                                {
                                    String[] split = string.split("_");
                                    modelIds[1] = Integer.parseInt(split[split.length - 1]);
                                    continue;
                                }

                                if (maleItem && string.startsWith("manwear3="))
                                {
                                    String[] split = string.split("_");
                                    modelIds[2] = Integer.parseInt(split[split.length - 1]);
                                    continue;
                                }

                                if (!maleItem && string.startsWith("womanwear="))
                                {
                                    String replaced = string.replace(",", "_");
                                    String[] split = replaced.split("_");
                                    modelIds[0] = Integer.parseInt(split[split.length - 2]);
                                    continue;
                                }

                                if (!maleItem && string.startsWith("womanwear2="))
                                {
                                    String[] split = string.split("_");
                                    modelIds[1] = Integer.parseInt(split[split.length - 1]);
                                    continue;
                                }

                                if (!maleItem && string.startsWith("womanwear3="))
                                {
                                    String[] split = string.split("_");
                                    modelIds[2] = Integer.parseInt(split[split.length - 1]);
                                    continue;
                                }
                            }

                            matcher = recolFrom.matcher(string);

                            if (matcher.matches())
                            {
                                String[] split = string.split("=");
                                recolourFrom.add(Integer.parseInt(split[1]));
                                continue;
                            }

                            matcher = recolTo.matcher(string);

                            if (matcher.matches())
                            {
                                String[] split = string.split("=");
                                recolourTo.add(Integer.parseInt(split[1]));
                            }
                        }

                        int size = recolourFrom.size();
                        short[] recolourFromArray = new short[size];
                        short[] recolourToArray = new short[size];
                        for (int e = 0; e < size; e++) {
                            int from = recolourFrom.get(e);
                            recolourFromArray[e] = (short) from;
                            int to = recolourTo.get(e);
                            recolourToArray[e] = (short) to;
                        }

                        for (int id : modelIds)
                        {
                            if (id > 0)
                            {
                                modelStatsItems.add(new ModelStats(id, recolourFromArray, recolourToArray));
                            }
                        }

                        if (i == patterns.length - 1)
                        {
                            break;
                        }
                    }
                }
            }
            br.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }


        //for KitIds
        int[] kitId = new int[kitList.size()];
        for (int i = 0; i < kitList.size(); i++)
        {
            kitId[i] = kitList.get(i);
        }

        try
        {
            URL url = new URL("https://gitlab.com/waliedyassen/cache-dumps/-/raw/master/dump.idk");
            URLConnection connection = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String s = "";
            Pattern[] patterns = new Pattern[kitId.length];

            for (int i = 0; i < kitId.length; i++)
            {
                int item = kitId[i];
                Pattern kitPattern = Pattern.compile("\\[.+_" + item + "]");
                patterns[i] = kitPattern;
            }

            while ((s = br.readLine()) != null)
            {
                for (int i = 0; i < patterns.length; i++)
                {
                    Pattern pattern = patterns[i];
                    Matcher matcher = pattern.matcher(s);
                    if (matcher.matches())
                    {
                        System.out.println("Match found: " + s);
                        int[] modelIds = new int[2];
                        ArrayList<Integer> recolourFrom = new ArrayList<>();
                        ArrayList<Integer> recolourTo = new ArrayList<>();


                        String string = "";
                        while (!(string = br.readLine()).equals(""))
                        {
                            if (string.startsWith("model1="))
                            {
                                String[] split = string.split("_");
                                modelIds[0] = Integer.parseInt(split[split.length - 1]);
                                continue;
                            }

                            if (string.startsWith("model2="))
                            {
                                String[] split = string.split("_");
                                modelIds[1] = Integer.parseInt(split[split.length - 1]);
                                continue;
                            }

                            matcher = recolFrom.matcher(string);

                            if (matcher.matches())
                            {
                                String[] split = string.split("=");
                                recolourFrom.add(Integer.parseInt(split[1]));
                                continue;
                            }

                            matcher = recolTo.matcher(string);

                            if (matcher.matches())
                            {
                                String[] split = string.split("=");
                                recolourTo.add(Integer.parseInt(split[1]));
                            }
                        }

                        int size = recolourFrom.size();
                        short[] recolourFromArray = new short[size];
                        short[] recolourToArray = new short[size];
                        for (int e = 0; e < size; e++)
                        {
                            int from = recolourFrom.get(e);
                            recolourFromArray[e] = (short) from;
                            int to = recolourTo.get(e);
                            recolourToArray[e] = (short) to;
                        }

                        for (int id : modelIds)
                        {
                            if (id > 0)
                            {
                                modelStatsItems.add(new ModelStats(id, recolourFromArray, recolourToArray));
                                /*
                                ModelData modelData = client.loadModelData(id);
                                short[] colors = modelData.getFaceColors();
                                for (short d : colors)
                                {
                                    System.out.println("id: " + id + ", " + d);
                                }

                                 */
                            }
                        }

                        if (i == patterns.length - 1)
                        {
                            break;
                        }
                    }
                }
            }
            br.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        return modelStatsItems.toArray(new ModelStats[0]);
    }
}