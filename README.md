# Versioning
Version framework.

# Introduction
Framework designed and implemented to act as the interceptor to the REST service versioning proposal.

The interceptor transform input object version, received from the client request, to an object version that is currently used by the executable code. And, also, it transform the object version returned from the executable code, to a response object version that is accepted by the client.

<img align="center" width="600" height="314" src="https://github.com/HaroldoMacedo/Version/blob/master/images/Interceptor.PNG" >

# Goals
* Create a versioning framework that are simple to use, easily evolvable and reliable
* Implemented using the SOLID principles
* To be used in the service versioning as the interceptor component

# Premises
* POJO
  * Java only, no extra libraries


