package com.shmuel.mastermind.data.file;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.shmuel.helpers.beans.Wrapper;
import com.shmuel.iterables.logic.IteratorClosableReader;
import com.shmuel.mastermind.shared.beans.Option;
import com.shmuel.mastermind.shared.interfaces.IOptionProvider;
import com.shmuel.iterables.interfaces.IteratorClosable;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

class OptionProviderFile implements IOptionProvider
{
    private final File file;
    private final String fileName;
    private boolean readOnly;
    private int count;
    private DataOutputStream writer;
    private int arrayLength;
    private static Random random = new Random(System.currentTimeMillis());

    OptionProviderFile(String fileName,boolean readOnly)
    {
        this.fileName=fileName;
        this.file = new File(ManagerDataProviderFile.instance.getDirRoot(), fileName);
        this.readOnly = readOnly;
    }
    OptionProviderFile(String fileName, int arrayLength)
    {
        this.fileName=fileName;
        this.file = new File(ManagerDataProviderFile.instance.getDirRoot(), fileName);
        this.readOnly = true;
        this.arrayLength=arrayLength;
        this.count= file.exists()? (int) (file.length() / arrayLength) :0;
    }

    @Override
    public void add(Option o) throws IOException
    {
        if (arrayLength == 0)
            arrayLength = o.getColors().length;
        if (writer == null)
            throw new IOException("cannot add option without starting write");
        writer.write(o.getColors());
        count++;
    }

    @Override
    public int size()
    {
        return count;
    }

    @Override
    public Option get(int index) throws IOException
    {
        if (index >= count)
            throw new IndexOutOfBoundsException("Index: "+index+", Size: "+count);

        DataInputStream reader = new DataInputStream(new FileInputStream(file));
        try
        {
            int skipped;
            if (count == 1)
            {
                skipped = 0;
            }
            else
            {
                int skip = random.nextInt(count);
                skipped = reader.skipBytes(skip * arrayLength);
            }
            Wrapper<Option> optionRef = new Wrapper<>();
            if (!tryReadOption(reader, optionRef))
                throw new RuntimeException("skip went wrong, no more bytes to read. count:"+count+", arrayLength:"+arrayLength+",skipped:"+skipped+", available:"+reader.available());
            return optionRef.getItem();
        }
        finally
        {
            reader.close();
        }
    }

    @Override
    public void startWrite() throws IOException
    {
        this.writer = new DataOutputStream(new BufferedOutputStream (new FileOutputStream(file)));
    }

    @Override
    public void endWrite() throws IOException
    {
        this.writer.close();
    }

    @Override
    public boolean clear()
    {
        if(readOnly)
            return false;
        count = 0;
        return file.delete();
    }

    private boolean tryReadOption(DataInputStream reader, Wrapper<Option> optionRef) throws IOException
    {
        if (arrayLength == 0)
            return false;
        byte[] colors = new byte[arrayLength];
        int read = reader.read(colors);
        if (read == -1)
            return false;
        Option option = new Option(colors);
        optionRef.setItem(option);
        return true;
    }


    @NonNull
    @Override
    public IteratorClosable<Option> iterator()
    {
        return new IteratorClosableReader<Option>(file)
        {
            @Override
            protected boolean tryReadNext(Wrapper<Option> nextRef) throws IOException
            {
                return tryReadOption(reader, nextRef);
            }
        };
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.fileName);
        dest.writeByte((byte) (this.readOnly?1:0));
    }

    protected OptionProviderFile(Parcel in)
    {
        this.fileName = in.readString();
        this.readOnly=in.readByte()==1;
        this.file = new File(ManagerDataProviderFile.instance.getDirRoot(), fileName);

    }

    public static final Creator<OptionProviderFile> CREATOR = new Creator<OptionProviderFile>()
    {
        @Override
        public OptionProviderFile createFromParcel(Parcel in)
        {
            return new OptionProviderFile(in);
        }

        @Override
        public OptionProviderFile[] newArray(int size)
        {
            return new OptionProviderFile[size];
        }
    };

}
