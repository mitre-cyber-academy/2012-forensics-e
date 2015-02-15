//Copyright Karl Rasmussen 2006
// Source File Name:   eFLV.java

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.ArrayList;

public class eFLV extends Applet
    implements ActionListener
{

    public static void main(String args[])
    {
        try
        {
            System.out.println(FLV_p);
            String s = System.getProperty("java.library.path");
            String s1 = System.getProperty("java.version");
            String s2 = System.getProperty("os.name");
            String s3 = System.getProperty("os.version");
            String s4 = System.getProperty("os.arch");
            if(s2.indexOf("Windows") == 0)
                FLV_av = true;
            FLV_n = System.getProperty("file.separator");
            String s5 = "1.4.2_06";
            if(!s5.equals(s1) && s1.indexOf("1.5") > 0)
            {
                System.out.println("JVM is not correct");
                System.exit(0);
            }
            if(args.length < 5)
            {
                printUsage();
                System.exit(1);
            }
            eFLV FLV = new eFLV();
            FLV.initMain(args);
            FLV.encode();
        }
        catch(Exception e)
        {
            System.out.println("Unknown Exception Error in main");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public eFLV()
    {
        bInitMain = false;
        CntrA = 0;
        CntrV = 0;
        CntrT = 0;
        bgColor = 0;
    }

    public void initMain(String args[])
    {
        FLV_in = args[0];
        FLV_out = args[1];
        FLV_vkey = args[2];
        FLV_akey = args[3];
        FLV_offset = args[4];
        cwd = "";
    }


    private void msg(String str)
    {
        System.out.println((new StringBuilder()).append("Debug msg: ").append(str).toString());
    }



    private void encode()
    {
        try
        {
            rtnB = false;
            if(cwd.length() == 0)
                cwd = System.getProperty("user.dir");
            msg((new StringBuilder()).append("Current Working Directory: ").append(cwd).toString());
            Class clazz = java/lang/ClassLoader;
            Field field = clazz.getDeclaredField("sys_paths");
            boolean accessible = field.isAccessible();
            if(!accessible)
                field.setAccessible(true);
            field.set(clazz, null);
            System.setProperty("java.library.path", cwd);
            msg((new StringBuilder()).append("java.library.path: ").append(System.getProperty("java.library.path").toString()).toString());
            byte bKeyA[] = FLV_akey.getBytes();
            byte bKeyV[] = FLV_vkey.getBytes();
            int iOffset = Integer.parseInt(FLV_offset);
            File fin = new File(FLV_in);
            FileInputStream fis = new FileInputStream(fin);
            BufferedInputStream bis = new BufferedInputStream(fis);
            File fout = new File(FLV_out);
            FileOutputStream fos = new FileOutputStream(fout);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            boolean b = false;
            byte abyte[] = {
                70, 76, 86, 1, 5, 0, 0, 0, 9, 0, 
                0, 0, 0
            };
            byte abyte2[] = new byte[13];
            int i1 = fis.read(abyte2);
            for(int i = 0; i < 13; i++)
                if(abyte2[i] == abyte[i])
                    b = true;

            if(!b)
                System.exit(0);
            bos.write(abyte2, 0, i1);
            bos.flush();
            byte Tag = 0;
            int iTag = 0;
            boolean Done = false;
            int a = 0;
            byte abyte3[] = new byte[4];
            while(!Done) 
            {
                i1 = fis.read(abyte3);
                Tag = abyte3[0];
                if(Tag == 18)
                    CntrT++;
                else
                if(Tag == 8)
                {
                    CntrA++;
                    CntrT++;
                } else
                if(Tag == 9)
                {
                    CntrV++;
                    CntrT++;
                }
                if(abyte3[1] < 0)
                    a = 256 - abyte3[1];
                else
                    a = abyte3[1];
                iTag = a * 256 * 256;
                if(abyte3[2] < 0)
                    a = 256 - abyte3[2];
                else
                    a = abyte3[2];
                iTag += a * 256;
                if(abyte3[3] < 0)
                    a = 256 + abyte3[3];
                else
                    a = abyte3[3];
                iTag += a;
                byte abyte4[] = new byte[iTag + 11];
                int i12 = fis.read(abyte4);
                if(CntrV > iOffset && Tag == 8)
                {
                    abyte3[0] -= 2;
                    int j = 0;
                    for(int i = 0; i < i12; i++)
                    {
                        abyte4[i] = (byte)(abyte4[i] ^ bKeyA[j]);
                        if(++j > FLV_akey.length() - 1)
                            j = 0;
                    }

                }
                if(CntrV > iOffset && Tag == 9)
                {
                    abyte3[0] -= 2;
                    int j = 0;
                    for(int i = 0; i < i12; i++)
                    {
                        abyte4[i] = (byte)(abyte4[i] ^ bKeyV[j]);
                        if(++j > FLV_vkey.length() - 1)
                            j = 0;
                    }

                }
                bos.write(abyte3, 0, i1);
                bos.write(abyte4, 0, i12);
                bos.flush();
            }
            bos.close();
        }
        catch(IOException ioe)
        {
            msg((new StringBuilder()).append("Error reading entry ").append(FLV_in).toString());
        }
        catch(NullPointerException npe)
        {
            msg((new StringBuilder()).append("Null Pointer Exeception in encode ").append(npe).toString());
        }
        catch(Throwable t)
        {
            msg((new StringBuilder()).append("Throwable Exeception in encode ").append(t).toString());
        }
    }

    public static void printUsage()
    {
        System.out.println((new StringBuilder()).append("Usage: ").append(FLV_s).append(" <flvIn> <flvOut> <vKey> <aKey> <offset>").toString());
    }



    public byte encodeature[];
    private boolean rtnB;
    private Font f;
    private int bgColor;
    Button btnCreateFile_;
    private String msg_;
    private FileOutputStream fileoutputstream;
    private BufferedWriter bufferedwriter;
    private boolean bInitMain;
    private String cwd;
    private String FLV_in;
    private String FLV_out;
    private String FLV_vkey;
    private String FLV_akey;
    private String FLV_offset;
    private String FLV_f;
    private String FLV_g;
    private byte FLV_h[];
    private byte FLV_j[];
    private String FLV_k;
    private ArrayList FLV_l;
    private MessageDigest FLV_m;
    private static String FLV_n;
    private String FLV_o;
    private static String FLV_s;
    private static String FLV_p;
    private static String FLV_q = "K";
    private String FLV_r;
    private String FLV_u;
    private String FLV_v;
    private String FLV_w;
    private String FLV_x;
    private String FLV_y;
    private String FLV_z;
    private String FLV_aa;
    private static boolean FLV_av = false;
    private int CntrA;
    private int CntrV;
    private int CntrT;

    static 
    {
        FLV_s = "eFLV";
        FLV_p = (new StringBuilder()).append(FLV_s).append(" Copyright (c) ????? 2006 (www.??????.org)").toString();
    }
}
