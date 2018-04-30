package com.willows5.movies.data;

public class Movie {
    int    _nId;
    String _sTitle;
    String _sDate;
    String _sVote;
    String _sDesc;
    String _sPoster;

    public Movie(int nId, String sTitle, String sDate, String sVote, String sDesc, String sPoster) {
        _nId = nId;
        _sTitle = sTitle;
        _sDate = sDate;
        _sVote = sVote;
        _sDesc = sDesc;
        _sPoster = sPoster;
    }

    public int getId() {
        return _nId;
    }

    public String getTitle() {
        return _sTitle;
    }

    public String getDate() {
        return _sDate;
    }

    public String getVote() {
        return _sVote;
    }

    public String getDesc() {
        return _sDesc;
    }

    public String getPoster() {
        return _sPoster;
    }
}
