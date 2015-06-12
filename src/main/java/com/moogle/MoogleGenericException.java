// $Id$
/* 
*/

package com.moogle;

public class MoogleGenericException extends Exception 
{
    /**
	 *   Needed for some dumb reason
	 */
	private static final long serialVersionUID = -6657073414531719934L;
	private String WhatTheFuckIsWrong = "Generic Moogle Exception Error";

	public MoogleGenericException()
	{
        super();
    }

    public MoogleGenericException(String message) 
    {
        super(message);
        WhatTheFuckIsWrong = message;
    }

    public MoogleGenericException(Throwable t) 
    {
        super(t);
    }

	public String getWhatTheFuckIsWrong() 
	{
		return WhatTheFuckIsWrong;
	}

}
