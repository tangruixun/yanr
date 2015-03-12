package com.trx.yanr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.mail.Header;

import android.util.Log;
import android.util.SparseArray;

import com.sun.mail.dsn.MessageHeaders;

public class NNTPClientClass {
	Socket nntpclientsocket; 
	BufferedReader in; 
	BufferedWriter out;
	String buffer;
	NewsGroups newsgroups = new NewsGroups();
	
	public NNTPClientClass() {
		nntpclientsocket = null;
		in = null;
		out = null;
	}
	
	public Socket connect (String dstName, int dstPort) throws IOException {
		try { 
			nntpclientsocket = new Socket(dstName, dstPort); 
			in = new BufferedReader (new InputStreamReader(nntpclientsocket.getInputStream())); 
			out = new BufferedWriter (new OutputStreamWriter(nntpclientsocket.getOutputStream())); 
        } catch (UnknownHostException e) {
            Log.i("--->", "Don't know about host: hostname");
        } catch (IOException e) {
        	Log.i("--->", "Couldn't get I/O for the connection to: hostname");
        }
		return nntpclientsocket;
	}
	
	int open() throws IOException {
		buffer = in.readLine();
        Log.i("--->", buffer.charAt (0)+"");
		if (buffer.charAt(0) != '2') {
			Log.i("--->", "Error: news server not found.");

			return 1;
		}
		return 0;
	}
	
	int sendCommand(String cmd) throws IOException {
		Log.i("--->", "Sending command \"" + cmd + "\"...");
	    out.write(cmd + "\r\n");
	    out.flush();

	    if ((buffer = in.readLine()) == null) {
	        throw new IOException();
	    }
	    else {
	    	if (buffer.length() < 1) throw new IOException();
	    	String strRtnCode = buffer.substring(0, 3);
	    	if (buffer.charAt(0) != '2') {
	    	  Log.i("--->", buffer);
	    	  Log.i("--->", "error code "+strRtnCode);
	    	}
	    	else {
	    		Log.i("--->", strRtnCode);
	    	}
	    	return Integer.parseInt(strRtnCode);
	    }
	}
	
    String sendCommandRawReturn (String cmd) throws IOException {
        Log.i ("--->", "Sending command \"" + cmd + "\"...");
        out.write (cmd + "\r\n");
        out.flush ();

        if ( (buffer = in.readLine ()) == null) {
            throw new IOException ();
        } else {
            if (buffer.length () < 1)
                throw new IOException ();
            String strRtnCode = buffer.substring (0, 3);
            if (buffer.charAt (0) != '2') {
                Log.i ("--->", buffer);
                Log.i ("--->", "error code " + strRtnCode);
            } else {
                Log.i ("--->", strRtnCode);
            }
            return buffer;
        }
    }
	
	String getTextResponse() throws IOException {
		String text = "";

	    while(true) {
	    	buffer = in.readLine();
	    	
	    	if (buffer == null) throw new IOException("End of text not expected.");

	    	if (buffer.length() < 1)
			        continue;
	    	buffer += "\n";
	    	// check for end of text
			if (buffer.charAt(0) == '.') {
				if (buffer.charAt(1) != '.') {
					break; // no doubt, text ended
				}
		        // we've got a doubled period here
		        // collapse the doubled period to a single one
		        buffer = buffer.substring(1, buffer.length());

			}

			text += buffer;
		}
	    return text;
	}
	
	
	
//	list
//	215 Newsgroups in form "group high low status"
//	eternal-september.config 0000000453 0000000001 y
//	eternal-september.grouprequests 0000000310 0000000013 y
//	eternal-september.info 0000000005 0000000001 m
//	eternal-september.moderated 0000000046 0000000001 m
//	eternal-september.newusers 0000000568 0000000001 y
//	eternal-september.nocems 0000000023 0000000001 y
//	eternal-september.support 0000005493 0000000006 y
//	eternal-september.talk 0000000374 0000000016 y
//	eternal-september.test 0000004675 0000000049 y
//	eternal-september.where.are.all.the.newsgroups 0000000007 0000000003 n
//	.
	NewsGroups listNewsgroups() throws IOException {
		try {
			int rst = sendCommand("LIST");
			if (rst>299) {
				throw new IOException();
			}
		} catch(Throwable e) {
			Log.i("--->", e.getMessage());
			throw new IOException();
		}

		// get names of all newsgroups
		while(true) {
			buffer = in.readLine();
			if (buffer == null) throw new IOException("End of text not expected.");
			
			if (buffer.length() < 1)
				continue;

			if (buffer.charAt(0) == '.') // check for end of listing
			    break;

			StringTokenizer st = new StringTokenizer(buffer, " ");
			newsgroups.addElement(st.nextToken());
		}

		return newsgroups;
		// list newsgroups
		//for(int i = 0; i < newsgroups.size(); i++)
		//	Log.i("--->", newsgroups.elementAt(i).toString());
	}
	  
	void displayAllSelectedNewsgroups() throws IOException {
	    for(int i = 0; i < newsgroups.size(); i++)
			displayNewsgroup(newsgroups.elementAt(i).toString());
	}
	
	String listNewsgroup (String group) throws IOException {
	    try {
            int rst = sendCommand("LISTGROUP " + group);
            if (rst>299) {
                return "";
            }
            Log.i("--->", group);
        } catch (Exception e) {
            Log.i("--->", e.getMessage());
            return "";
        }
	    
        String allArticlesNumbers = getTextResponse();
        
        return allArticlesNumbers;

	}

	void displayNewsgroup(String group) throws IOException {
		try {
			int rst = sendCommand("GROUP " + group);
			if (rst>299) {
				return;
			}
			Log.i("--->", group);
		} /*catch(ZNewsCommandException e) {
			Log.i("--->", "Error: could not access newsgroup \"" + group +"\".");
			return;
		} */catch(IOException e) {
			Log.i("--->", e.getMessage());
			return;
	    }  
	  
	    StringTokenizer st = new StringTokenizer(buffer, " ");
	    st.nextToken();
	    st.nextToken();
	    int firstArticleNumber = Integer.valueOf((st.nextToken())),
	        lastArticleNumber = Integer.valueOf((st.nextToken()));
	    Log.i("--->", "First article #" + firstArticleNumber);
	    Log.i("--->", "Last article #" + lastArticleNumber);

	    while(true) {
	    try {
	    		int rst = sendCommand("HEAD");
				if (rst>299) {
					return;
				}
	    		String tmp = getTextResponse();
	    		Log.i("--->", tmp); // display header text
	    		rst = sendCommand("BODY");
				if (rst>299) {
					return;
				}
	    		tmp = getTextResponse();
	    		Log.i("--->", tmp); // display body text
	      } catch(Throwable e) {
	    	  Log.i("--->", e.getMessage());
	    	  // there's been a problem with the current article, let's try to get the next one
	      }

	      try {
	    	  int rst = sendCommand("NEXT");
	    	  if (rst>299) {
				 return;
	    	  }
	      } /*catch(ZNewsCommandException e) {
	    	  // "next" command failed
	    	  // looks like there are no more articles in the current newsgroup
	    	  Log.i("--->", e.getMessage());
	    	  return;
	      } */catch(IOException e) {
	    	  Log.i("--->", e.getMessage());
	    	  // an IO error, let's get out of here!
	    	  return;
	      }
	    }
	}  

	
	List<NNTPMessageHeader> displayNewsgroupHeads(String group) throws IOException {

		NNTPMessageHeader message_headers = new NNTPMessageHeader();
		List<NNTPMessageHeader> article_list = new ArrayList<NNTPMessageHeader>();
		
		try {
			int rst = sendCommand("GROUP " + group);
			if (rst>299) {
				throw new ZNewsCommandException();
			}
			Log.i("--->", group);
		} catch (ZNewsCommandException e) {
			Log.i("--->", "Error: could not access newsgroup \"" + group
					+ "\".");
			return article_list;
		} catch (IOException e) {
			Log.i("--->", e.getMessage());
			return article_list;
		}

		StringTokenizer st = new StringTokenizer(buffer, " ");
		st.nextToken();
		st.nextToken();
		int firstArticleNumber = Integer.valueOf((st.nextToken()));
		int lastArticleNumber = Integer.valueOf((st.nextToken()));
		Log.i("---> First article #", " " + firstArticleNumber);
		Log.i("---> Last article #", " " + lastArticleNumber);
		
		while (true) {
			try {
				int rst = sendCommand("HEAD");
				if (rst>299) {
					break;
				}
				String tmp = getTextResponse();
				
				InputStream is = new ByteArrayInputStream(tmp.getBytes());
				MessageHeaders headers = new MessageHeaders(is);
				
				Enumeration <Header> en = headers.getAllHeaders();
				while (en.hasMoreElements()) {
					Header h = (Header) en.nextElement();
					message_headers.setHeader(h.getName(), h.getValue()); // set all headers of one single message 
				}
				
				article_list.add(message_headers); // add to header array list
				
			} catch (Throwable e) {
				Log.i("--->", e.getMessage());
				// there's been a problem with the current article, let's try to
				// get the next one
			}

			try {
				int rst = sendCommand("next"); //next post header
				if (rst>299) {
					break;
				}
			} catch (ZNewsCommandException e) {
				// "next" command failed
				// looks like there are no more articles in the current
				// newsgroup
				Log.i("--->", e.getMessage());
				break;
			} catch (IOException e) {
				Log.i("--->", e.getMessage());
				// an IO error, let's get out of here!
				break;
			}
		}
		return article_list;
	}
	
	List <SparseArray<NNTPMessageHeader>> displayNewsgroupHeadsWithRawReturn (String group)
            throws IOException {

        List <SparseArray<NNTPMessageHeader>> headerList = new ArrayList <SparseArray<NNTPMessageHeader>> (); // all headers
        String [] returnArray = {};
        int articleId = 0;
        
        try {
            int rst = sendCommand ("GROUP " + group);

            if (rst > 299) {
                throw new ZNewsCommandException ();
            }
            Log.i ("--->", group);
        } catch (ZNewsCommandException e) {
            Log.i ("--->", "Error: could not access newsgroup \"" + group
                    + "\".");
            return headerList;
        } catch (IOException e) {
            Log.i ("--->", e.getMessage ());
            return headerList;
        }

        StringTokenizer st = new StringTokenizer (buffer, " ");
        st.nextToken ();
        st.nextToken ();
        int firstArticleNumber = Integer.valueOf ( (st.nextToken ()));
        int lastArticleNumber = Integer.valueOf ( (st.nextToken ()));
        Log.i ("---> First article #", " " + firstArticleNumber);
        Log.i ("---> Last article #", " " + lastArticleNumber);

        while (true) {
            SparseArray<NNTPMessageHeader> idHeaderMap = new SparseArray<NNTPMessageHeader> (); // single header
            NNTPMessageHeader message_headers = new NNTPMessageHeader ();
            try {
                String firstLine = sendCommandRawReturn ("HEAD");
                String strRtnCode = firstLine.substring(0, 3);
                if (firstLine.charAt(0) != '2') {
                  Log.i("--->", firstLine);
                  Log.i("--->", "error code "+strRtnCode);
                }
                else {
                    Log.i("--->", strRtnCode);
                    returnArray = firstLine.split (" ");
                    articleId = Integer.parseInt (returnArray [1]); // now ID
                }
                int rst = Integer.parseInt(strRtnCode); 
                if (rst > 299) {
                    break;
                }
                String tmp = getTextResponse ();

                InputStream is = new ByteArrayInputStream (tmp.getBytes ());
                MessageHeaders headers = new MessageHeaders (is);

                Enumeration <Header> en = headers.getAllHeaders ();
                while (en.hasMoreElements ()) {
                    Header h = (Header) en.nextElement ();
                    message_headers.setHeader (h.getName (), h.getValue ()); // set all headers of one single message
                }

                idHeaderMap.put (articleId, message_headers);
                headerList.add (idHeaderMap);

            } catch (Throwable e) {
                Log.i ("--->", e.getMessage ());
                // there's been a problem with the current article, let's try to
                // get the next one
            }

            try {
                int rst = sendCommand ("next"); // next post header
                if (rst > 299) {
                    break;
                }
            } catch (ZNewsCommandException e) {
                // "next" command failed
                // looks like there are no more articles in the current
                // newsgroup
                Log.i ("--->", e.getMessage ());
                break;
            } catch (IOException e) {
                Log.i ("--->", e.getMessage ());
                // an IO error, let's get out of here!
                break;
            }
        }
        return headerList;
    }
	  
	void handleParameters(String args[]) throws IOException {
		if (args[1].equalsIgnoreCase("-l")) {
			listNewsgroups();
		} else if (args[1].equalsIgnoreCase("-d")) {
			for (int i = 2; i < args.length; i++)
				newsgroups.addElement(args[i]);

			if (newsgroups.size() < 1) {
				Log.i("--->", "Error: no newsgroups specified.");
			}

			displayAllSelectedNewsgroups();
		} else {
			Log.i("--->", "Error: invalid parameters.");
		}
	}

    public void displayNewsgroupHeadsWithArticleNo (String groupname,
            int articleNo) {
        
    }
	  

	  
		
//		if (nntpclientsocket != null && in != null && out != null) {
//            try {
//            	 
//			    String thisLine;
//				while ((thisLine = in.readLine()) != null) {
//					Log.i("--->", thisLine);
//				}
//				out.writeBytes("LIST\n"); 
//				while ((thisLine = in.readLine()) != null) {
//					Log.i("--->", thisLine);
//				}
//				out.close();
//				in.close();
//				nntpclientsocket.close();   
//            } catch (IOException e) {
//            	e.printStackTrace();
//            }
//		}
}
