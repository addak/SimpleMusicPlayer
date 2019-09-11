package com.bignerdranch.simplemusicplayer;

public class AudioFile {
    private String mTitle;
    private String mPath;
    private String mArtist;
    private String mAlbum;

    public AudioFile(){

    }

    public AudioFile(String title, String artist, String album, String path )
    {
        mTitle = title;
        mPath = path;
        mArtist = artist;
        mAlbum = album;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public void setPath( String path)
    {
        mPath = path;
    }

    public void setArtist( String artist)
    {
        mArtist = artist;
    }

    public void setAlbum( String album)
    {
        mAlbum = album;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public String getmPath()
    {
        return mPath;
    }

    public String getmArtist()
    {
        return mArtist;
    }
    public String getmAlbum()
    {
        return mAlbum;
    }
}
