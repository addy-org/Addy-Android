package reverieworks.addy.Activity;

/**
 * Created by user on 3/25/2017.
 */

public class ACodeJSON
{
    private String Acode;

    private String _id;

    private String UserName;

    private String Special_Name;

    public String getAcode ()
    {
        return Acode;
    }

    public void setAcode (String Acode)
    {
        this.Acode = Acode;
    }

    public String get_id ()
    {
        return _id;
    }

    public void set_id (String _id)
    {
        this._id = _id;
    }

    public String getUserName ()
    {
        return UserName;
    }

    public void setUserName (String UserName)
    {
        this.UserName = UserName;
    }

    public String getSpecial_Name ()
    {
        return Special_Name;
    }

    public void setSpecial_Name (String Special_Name)
    {
        this.Special_Name = Special_Name;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [Acode = "+Acode+", _id = "+_id+", UserName = "+UserName+", Special_Name = "+Special_Name+"]";
    }
}
