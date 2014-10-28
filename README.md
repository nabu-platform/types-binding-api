# Type Binding

This API focuses on binding formatted data to types as expressed by the types-api, it allows you to marshal an instance to data and unmarshal an instance from data.
In the unmarshalling phase there is a big emphasis on *transparent* large data support 

## Big Data

One of the biggest problems with existing tools is the support for large files. Most tools have some support for it, but it generally requires a radically different approach to both the parsing step and the steps afterwards. This is frustrating because it requires two sets of skills and if a file that started off small eventually becomes big you may need to refactor large portions of the project.

Let's take as a simple example an XML file. There are (sticking to the main java API) two ways to parse this easily:

- DOM Document: this will simply map the entire XML to memory and give you easy access to tags, attributes and the like. Supports XPath for lookup
- JAXB: this will bind it to one or more beans. You lose XPath support but you have a more structured overview

After a while the XML file that started off as 50kb has grown to a healthy 200MB, either due to an oversight in the analysis phase or simply because more data has accumulated over time.
If you try to parse 200MB fully in memory, your server will probably flatline (unless you are running on beefy hardware). What are your options?

- SAX: streams through a file and triggers your handler when an XML event occurs (e.g. a new tag, attribute, namespace,...)
- StAX: same principle as SAX but instead of changes being pushed to your code, you can pull the changes one at a time.

You can use either to step through the XML and then use your DOM/JAXB handling for smaller portions of the XML. What are the downsides of this approach?

- It takes a different API and a lot more technical expertise to parse it correctly
- You lose contextual information. For example suppose you have a header, a large amount of elements and a footer. When looping over the elements you have no idea of:
	- How many elements are there?
	- What data is in the footer?
	- Even keeping the correct header information takes custom code
- You lose random access, you want to go back to the previous element? You will need custom code to for example keep the last one in memory. You want to jump to a random element? You will have to reparse to the correct element every time.	This requires being able to reopen/reset the stream etc...
- It is a very XML-centric solution, if you have the same example with flat files, you will have to find custom solutions there

Additionally if you have nested large data objects, this can become very difficult or (in some tools) even impossible.

## A Solution

The solution baked into this framework is a windowed approach where a sliding window offers you access to your data. When simply looping over the entries, the window will gradually move but it is also capable of jumping to any random point in the data. Additionally you can access any data after the windowed element. As a matter of fact, from the perspective of the developer working with the instance, he has no idea the window logic is happening in the background, it is fully transparent.
 
The framework accomplishes this by parsing the entire file and at the same time writing it to a (configurable) storage. Anything outside the window is not kept in memory but instead offsets are kept. Whenever an element outside the window is requested, it will use these offsets to reparse the required pieces quickly.

There are a few advantages to this approach:

- Large data handling becomes a configuration option. If you define a window, one will be used in the background, otherwise everything will be loaded into memory
- It not only works for XML, but also for example for flat file where you can take large CSV's or fixed length files and load them in a windowed fashion
- You retain the contextual information, the footer in the above example is accessible at any time.
- All other code is oblivious to the large data handling. Whether you are iterating over the elements, accessing them randomly or even running the evaluator engine on them, everything will keep working because it is fully transparent.

## Example of Large Data Handling

To take an example from the flat file binding, you can unmarshal some data like this:

```
ComplexContent content = flatBinding.unmarshal(input, new Window[0]);
``` 

This will not use any windows which means all records in the flat file will be loaded into memory.

Alternatively you can activate a window:

```
ComplexContent content = flatBinding.unmarshal(input, new Window[] { new Window("company/employees", 6, 3) });
```

Notice the following:

- The window is defined using the path in the **target** type which allows a format-agnostic definition of a window
- The second parameter indicates that at any given time only 6 records will be kept in memory
- The third parameter indicates that when a user requests a record outside of the window, it will actually unload the 3 oldest ones and load 3 new records starting from the requested position

These settings allow you to tweak the parser to minimize memory usage or optimize speed while sacrificing memory. You can tweak for random access or sequential access by altering the third parameter. For example a batch size equal to the window size is ideal for sequential access but terrible if you always want to access the previous element.